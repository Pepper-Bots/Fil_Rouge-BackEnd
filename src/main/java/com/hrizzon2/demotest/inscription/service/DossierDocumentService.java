package com.hrizzon2.demotest.inscription.service;

import com.hrizzon2.demotest.common.service.FichierService;
import com.hrizzon2.demotest.document.dao.DocumentDao;
import com.hrizzon2.demotest.document.dao.StatutDocumentDao;
import com.hrizzon2.demotest.document.dto.DocumentSummaryDto;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.StatutDocument;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.inscription.dao.DossierDao;
import com.hrizzon2.demotest.inscription.dao.InscriptionDao;
import com.hrizzon2.demotest.inscription.dao.StatutDossierDao;
import com.hrizzon2.demotest.inscription.model.Dossier;
import com.hrizzon2.demotest.inscription.model.StatutDossier;
import com.hrizzon2.demotest.inscription.model.enums.StatutInscription;
import com.hrizzon2.demotest.user.model.Stagiaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DossierDocumentService {

    private final DocumentDao documentDao;
    private final DossierDao dossierDao;
    private final InscriptionDao inscriptionDao;
    private final StatutDocumentDao statutDocumentDao;
    private final StatutDossierDao statutDossierDao;
    private final FichierService fichierService;

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
    public DossierDocumentService(DocumentDao documentDao,
                                  DossierDao dossierDao, InscriptionDao inscriptionDao,
                                  StatutDocumentDao statutDocumentDao, StatutDossierDao statutDossierDao,
                                  FichierService fichierService) {
        this.documentDao = documentDao;
        this.dossierDao = dossierDao;
        this.inscriptionDao = inscriptionDao;
        this.statutDocumentDao = statutDocumentDao;
        this.statutDossierDao = statutDossierDao;
        this.fichierService = fichierService;
    }


    public void uploadDocument(Integer dossierId, MultipartFile file, TypeDocument type) {
        try {
            // Nettoyer le nom du fichier
            String cleanFileName = fichierService.sanitizeFileName(file.getOriginalFilename());

            // Stocker le fichier dans le dossier privé
            fichierService.uploadToLocalFileSystem(file, cleanFileName, false);

            // Récupérer le dossier
            Dossier dossier = dossierDao.findById(dossierId)
                    .orElseThrow(() -> new IllegalArgumentException("Dossier introuvable"));

            // Récupérer le statut EN_ATTENTE
            StatutDocument statutEnAttente = statutDocumentDao.findByNom("EN_ATTENTE")
                    .orElseThrow(() -> new IllegalStateException("Statut EN_ATTENTE introuvable"));

            // Créer et sauvegarder le document
            Document document = new Document();
            document.setDossier(dossier);
            document.setNomFichier(cleanFileName);
            document.setType(type);
            document.setStatut(statutEnAttente);
            document.setDateDepot(LocalDateTime.now());

            documentDao.save(document);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier", e);
        }
    }

    public List<Document> getDocumentsByStagiaire(Integer idStagiaire) {
        return documentDao.findByDossierStagiaireId(idStagiaire);
    }

    public Dossier getDossierCompletPourStagiaire(Integer idStagiaire) {
        List<Document> docs = getDocumentsByStagiaire(idStagiaire);
        if (docs.isEmpty()) return null;

        Dossier dossier = dossierDao.findByStagiaireId(idStagiaire)
                .orElseThrow(() -> new IllegalArgumentException("Dossier introuvable"));

        dossier.setDocuments(docs);

        boolean complet = listeDocsObligatoires.stream()
                .allMatch(type -> docs.stream()
                        .anyMatch(doc -> doc.getType() == type && "VALIDÉ".equals(doc.getStatut().getNom())));

        StatutDossier statut = statutDossierDao.findByNomStatut(complet ? "COMPLET" : "INCOMPLET")
                .orElseThrow(() -> new IllegalStateException("Statut dossier introuvable"));

        dossier.setStatutDossier(statut);

        if (complet) {
            inscriptionDao.findByStagiaireIdAndStatut(idStagiaire, StatutInscription.EN_ATTENTE)
                    .ifPresent(inscription -> {
                        inscription.setStatut(StatutInscription.VALIDEE);
                        inscriptionDao.save(inscription);
                    });
        }
        return dossierDao.save(dossier);
    }

    public List<DocumentSummaryDto> getStatutDocumentsDossier(Stagiaire stagiaire, Formation formation) {
        // 1. Récupérer la liste des documents obligatoires pour cette formation
        List<TypeDocument> documentsAttendus = formation.getListeDocumentsObligatoires();

        // 2. Récupérer tous les documents déjà transmis par le stagiaire pour cette formation (via dossier)
        Dossier dossier = dossierDao.findByStagiaireId(stagiaire.getId())
                .orElse(null);

        List<Document> documentsTransmis = (dossier != null) ?
                documentDao.findByDossierStagiaireId(dossier.getId()) :
                List.of();

        // 3. Préparer la liste résultat
        List<DocumentSummaryDto> statutDocs = new ArrayList<>();

        for (TypeDocument type : documentsAttendus) {
            // Chercher document transmis correspondant à ce type
            Document doc = documentsTransmis.stream()
                    .filter(d -> d.getType() == type)
                    .findFirst()
                    .orElse(null);

            DocumentSummaryDto dto = new DocumentSummaryDto();
            dto.setTypeDocument(type);

            if (doc == null) {
                // Document manquant
                dto.setStatut("MANQUANT");
            } else {
                dto.setStatut(doc.getStatut().getNom());
                dto.setCommentaire(doc.getCommentaire());
                dto.setDateDepot(doc.getDateDepot());
                dto.setFichier(doc.getNomFichier());
            }
            statutDocs.add(dto);
        }
        return statutDocs;
    }

    public boolean supprimerDocument(Integer documentId) {
        var docOpt = documentDao.findById(documentId);
        if (docOpt.isEmpty()) return false;
        Document doc = docOpt.get();

        // Supprimer fichier physique
        try {
            fichierService.deleteFile(doc.getNomFichier());
        } catch (Exception ignored) {
            // Log l'erreur mais continuer
        }
        documentDao.delete(doc);
        return true;
    }

    public void validerDocument(Integer documentId, String statut, String commentaire) {
        Document doc = documentDao.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document introuvable"));

        String statutActuel = doc.getStatut() != null ? doc.getStatut().getNom() : null;

        if (!"EN_ATTENTE".equals(doc.getStatut().getNom())) {
            throw new IllegalArgumentException("Document non en attente");
        }

        // Gestion des différents statuts possibles
        final String cible = statut != null ? statut.toUpperCase().trim() : "";
        final String statutCible;
        if ("VALIDE".equals(cible) || "VALIDÉ".equals(cible)) {
            statutCible = "VALIDÉ";
        } else if ("REJETE".equals(cible) || "REJETÉ".equals(cible)) {
            statutCible = "REJETÉ";
        } else {
            throw new IllegalArgumentException("Statut non supporté: " + statut);
        }

        StatutDocument nouveauStatut = statutDocumentDao.findByNom(statutCible)
                .orElseThrow(() -> new IllegalStateException("Statut " + statutCible + " introuvable"));

        // Mise à jour du document
        doc.setStatut(nouveauStatut);
        doc.setCommentaire(commentaire); // Ajout du commentaire
        documentDao.save(doc);

        // Recalcul du statut dossier
        if (doc.getDossier() != null && doc.getDossier().getStagiaire() != null) {
            Integer stagiaireId = doc.getDossier().getStagiaire().getId();
            getDossierCompletPourStagiaire(stagiaireId);
        }
    }

    public List<Document> getPendingDocuments() {
        return documentDao.findByStatutNom("EN_ATTENTE");
    }


    public void rejeterDocument(Integer documentId) {
        Document doc = documentDao.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document introuvable"));

        if (!"EN_ATTENTE".equals(doc.getStatut().getNom())) {
            throw new IllegalArgumentException("Document non en attente");
        }

        StatutDocument statutRejete = statutDocumentDao.findByNom("REJETÉ")
                .orElseThrow(() -> new IllegalStateException("Statut REJETÉ introuvable"));

        doc.setStatut(statutRejete);
        documentDao.save(doc);

        // Recalcul du statut dossier etc. à implémenter selon besoin
    }


    public Optional<Document> findById(Integer documentId) {
        return documentDao.findById(documentId);
    }

    public Optional<Document> findDocumentById(Integer documentId) {
        return documentDao.findById(documentId);
    }
}
