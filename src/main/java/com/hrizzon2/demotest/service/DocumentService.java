package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.dao.*;
import com.hrizzon2.demotest.model.*;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service métier pour la gestion des documents soumis par les stagiaires,
 * leur validation/rejet, et la mise à jour du statut global du dossier.
 */
@Service
public class DocumentService {

    private final DocumentDao documentDao;
    private final DossierDao dossierDao;
    private final StagiaireDao stagiaireDao;
    private final InscriptionDao inscriptionDao;
    private final FichierService fichierService;
    private final StatutDocumentDao statutDocumentDao;
    private final StatutDossierDao statutDossierDao;

    /**
     * Liste statique
     * des types de documents obligatoires.
     */
    private final List<TypeDocument> listeDocsObligatoires = List.of(
            TypeDocument.PIECE_IDENTITE,
            TypeDocument.DIPLOME_BAC,
            TypeDocument.DIPLOME_BAC_2,
            TypeDocument.DIPLOME_BAC_3,
            TypeDocument.CV,
            TypeDocument.LETTRE_MOTIVATION,
            TypeDocument.JUSTIF_SITUATION,
            TypeDocument.PORTFOLIO,
            TypeDocument.ATTEST_RESP_CIVILE,
            TypeDocument.AUTRE
    );

    @Autowired
    public DocumentService(DocumentDao documentDao, DossierDao dossierDao, StagiaireDao stagiaireDao, InscriptionDao inscriptionDao, FichierService fichierService, StatutDocumentDao statutDocumentDao, StatutDossierDao statutDossierDao) {
        this.documentDao = documentDao;
        this.dossierDao = dossierDao;
        this.stagiaireDao = stagiaireDao;
        this.inscriptionDao = inscriptionDao;
        this.fichierService = fichierService;
        this.statutDocumentDao = statutDocumentDao;
        this.statutDossierDao = statutDossierDao;
    }

    /**
     * Upload un document pour le stagiaire donné, en spécifiant son type.
     * <ol>
     *   <li>On sanitize le nom de fichier et on l'enregistre via FichierService.</li>
     *   <li>On récupère (ou crée) le Dossier associé au Stagiaire.</li>
     *   <li>On crée une entité Document(référence vers dossier, type, statut=EN_ATTENTE).</li>
     *   <li>On sauvegarde le Document en base.</li>
     * </ol>
     *
     * @param stagiaireId ID du stagiaire (Integer)
     * @param type        Type du document (Enum TypeDocument)
     * @param fichier     Le fichier MultipartFile à stocker
     * @throws IOException              en cas de problème d’écriture sur le système de fichiers
     * @throws IllegalArgumentException si le stagiaire n’existe pas ou si le type n’est pas autorisé
     */
    public void uploadDocument(Integer stagiaireId,
                               MultipartFile fichier,
                               TypeDocument type) throws IOException {

        // 1. Vérifier que le stagiaire existe
        Stagiaire stagiaire = stagiaireDao.findById(stagiaireId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stagiaire introuvable (ID=" + stagiaireId + ")"));

        // 2. Vérifier que le typeDocument est bien parmi les obligatoires
        if (!listeDocsObligatoires.contains(type)) {
            throw new IllegalArgumentException("Type de document non autorisé : " + type);
        }

        // 3. Vérifier s’il n’y a pas déjà un Document du même type en attente ou validé
        boolean dejaSoumis = documentDao
                .findByDossierStagiaireIdAndType(stagiaireId, type)
                .stream()
                // À présent, doc est bien un Document, pas un Object
                .anyMatch(doc -> !doc.getStatut().getNom().equals("REJETÉ"));
        if (dejaSoumis) {
            throw new IllegalArgumentException(
                    "Un document de type " + type + " est déjà en attente ou validé."
            );
        }

        // 4. Sauvegarder le fichier sur le disque/local (via votre FichierService)
        String safeName = fichierService.sanitizeFileName(fichier.getOriginalFilename());
        fichierService.uploadToLocalFileSystem(fichier, safeName, false);

        // 5. Charger (ou créer) le Dossier associé au stagiaire
        // On récupère, s’il existe, le dossier du stagiaire
        Dossier dossier = dossierDao.findByStagiaireId(stagiaireId)
                .orElseGet(() -> {
                    // Créer un nouveau Dossier s’il n’existe pas
                    Dossier d = new Dossier();
                    d.setStagiaire(stagiaire);
                    // Initialiser le statut du dossier à « INCOMPLET » (pour commencer)
                    StatutDossier statIncomplet = statutDossierDao
                            .findByNomStatut("INCOMPLET")
                            .orElseThrow(() -> new IllegalStateException(
                                    "StatutDossier « INCOMPLET » introuvable en base !"));
                    d.setStatutDossier(statIncomplet);
                    return dossierDao.save(d);
                });

        // 6. Récupérer l'entité StatutDocument « EN_ATTENTE »
        StatutDocument statutEnAttente = statutDocumentDao.findByNom("EN_ATTENTE")
                .orElseThrow(() -> new IllegalStateException(
                        "StatutDocument « EN_ATTENTE » introuvable en base !"));

        // 7. Créer et sauver l’entité Document
        Document document = new Document();
        document.setDossier(dossier);
        document.setNomFichier(safeName);
        document.setType(type);
        document.setStatut(statutEnAttente);
        document.setDateDepot(LocalDateTime.now());

        documentDao.save(document);
    }

    /**
     * Renvoie la liste de tous les documents déjà soumis par le stagiaire.
     *
     * @param stagiaireId ID du stagiaire
     * @return Liste de Document (tous statuts confondus)
     */
    public List<Document> getDocumentsByStagiaire(Integer stagiaireId) {
        return documentDao.findByDossierStagiaireId(stagiaireId);

    }

    /**
     * Construit et retourne un Dossier « complet » pour le stagiaire donné.
     * <ol>
     *   <li>Charge tous les Documents du stagiaire.</li>
     *   <li>Si aucun document n’existe, renvoie null (=> 404 dans le contrôleur).</li>
     *   <li>Sinon, charge (ou crée) l’entité Dossier et y associe la liste de documents.</li>
     *   <li>Calcule le statut global du Dossier (INCOMPLET ou COMPLET).</li>
     *   <li>Si COMPLET, valide l’Inscription EN_ATTENTE du stagiaire.</li>
     *   <li>Sauvegarde enfin le Dossier avec son nouveau statut.</li>
     * </ol>
     *
     * @param stagiaireId ID du stagiaire
     * @return le Dossier mis à jour (ou null s’il n’y a aucun document)
     */
    public Dossier getDossierCompletPourStagiaire(Integer stagiaireId) {
        // 1. Charger tous les documents du stagiaire
        List<Document> docs = documentDao.findByDossierStagiaireId(stagiaireId);
        if (docs.isEmpty()) {
            return null;
        }

        // 2. Charger le stagiaire
        Stagiaire stagiaire = stagiaireDao.findById((int) stagiaireId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stagiaire introuvable (ID=" + stagiaireId + ")"));

        // 3. Charger (ou créer) le dossier
        Dossier dossier = dossierDao.findByStagiaireId(stagiaireId)
                .orElseGet(() -> {
                    Dossier d = new Dossier();
                    d.setStagiaire(stagiaire);
                    StatutDossier statIncomplet = statutDossierDao
                            .findByNomStatut("INCOMPLET")
                            .orElseThrow(() -> new IllegalStateException(
                                    "StatutDossier « INCOMPLET » introuvable en base !"));
                    d.setStatutDossier(statIncomplet);
                    return dossierDao.save(d);
                });

        // 4. On rattache la liste de documents au dossier
        dossier.setDocuments(docs);

        // 5. Calcul du statut global du dossier
        boolean complet = listeDocsObligatoires.stream().allMatch(type ->
                docs.stream().anyMatch(doc ->
                        doc.getType() == type
                                && doc.getStatut().getNom().equals("VALIDÉ")
                )
        );
        StatutDossier nouveauStatut = statutDossierDao
                .findByNomStatut(complet ? "COMPLET" : "INCOMPLET")
                .orElseThrow(() -> new IllegalStateException(
                        "StatutDossier introuvable pour « " + (complet ? "COMPLET" : "INCOMPLET") + " »"
                ));
        dossier.setStatutDossier(nouveauStatut);

        // 6. Si COMPLET → valider l’inscription EN_ATTENTE associée
        if (complet) {
            inscriptionDao.findByStagiaireIdAndStatut(
                    stagiaireId,
                    StatutInscription.EN_ATTENTE
            ).ifPresent(inscription -> {
                inscription.setStatut(StatutInscription.VALIDEE);
                inscription.setDateValidation(java.time.LocalDate.now());
                inscription.setDateModification(java.time.LocalDate.now());
                inscriptionDao.save(inscription);
            });
        }

        // 6. Sauvegarder et renvoyer
        return dossierDao.save(dossier);
    }

    /**
     * Renvoie la liste de tous les documents EN_ATTENTE (non encore validés),
     * afin que l’admin puisse les examiner en masse.
     *
     * @return Liste de Document dont statut.nom == "EN_ATTENTE"
     */
    public List<Document> getPendingDocuments() {
        return documentDao.findByStatutNom("EN_ATTENTE");
    }

    /**
     * Passe le document en statut VALIDÉ, puis recalcule le dossier global.
     *
     * @param documentId ID du document à valider
     * @throws IllegalArgumentException si le document n’existe pas ou n’est pas en attente
     */
    public void validerDocument(Integer documentId) {
        Document doc = documentDao.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Document introuvable (ID=" + documentId + ")"));

        if (!doc.getStatut().getNom().equals("EN_ATTENTE")) {
            throw new IllegalArgumentException("Le document n'est pas en attente");
        }

        // 1. Changer le statut en VALIDÉ
        StatutDocument statutValide = statutDocumentDao.findByNom("VALIDÉ")
                .orElseThrow(() -> new IllegalStateException(
                        "StatutDocument « VALIDÉ » introuvable en base !"));
        doc.setStatut(statutValide);
        documentDao.save(doc);

        // 2. Recalculer le dossier du stagiaire
        getDossierCompletPourStagiaire(doc.getDossier().getStagiaire().getId());
    }

    /**
     * Passe le document en statut REJETÉ, puis recalcule le dossier global.
     *
     * @param documentId ID du document à rejeter
     * @throws IllegalArgumentException si le document n’existe pas ou n’est pas en attente
     */

    public void rejeterDocument(Integer documentId) {

        Document doc = documentDao.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Document introuvable (ID=" + documentId + ")"));

        if (!doc.getStatut().getNom().equals("EN_ATTENTE")) {
            throw new IllegalArgumentException("Le document n'est pas en attente");
        }

        // 1. Changer le statut en REJETÉ
        StatutDocument statutRejete = statutDocumentDao.findByNom("REJETÉ")
                .orElseThrow(() -> new IllegalStateException("Statut REJETÉ introuvable en base !"));
        doc.setStatut(statutRejete);
        documentDao.save(doc);

        // 2. Recalculer le dossier du stagiaire (il restera INCOMPLET car un doc a été rejeté)
        getDossierCompletPourStagiaire(doc.getDossier().getStagiaire().getId());
    }
}

// TODO
//  - uploadDocument(MultipartFile, dossierId?, evenementId?, stagiaireId)
//  Conserver + Fusion
//  Logique stockage + association documents / dossiers / évènements
//  - getDocumentsByStagiaire, getDocumentsByDossier, getDocumentsByEvenement
//  - deleteDocument