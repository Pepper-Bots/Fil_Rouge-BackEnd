package com.hrizzon2.demotest.user.service;

import com.hrizzon2.demotest.document.dao.DocumentDao;
import com.hrizzon2.demotest.document.dao.StatutDocumentDao;
import com.hrizzon2.demotest.document.dto.DocumentAttenteDto;
import com.hrizzon2.demotest.document.dto.DocumentValidationRequestDto;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.StatutDocument;
import com.hrizzon2.demotest.formation.dao.FormationDao;
import com.hrizzon2.demotest.inscription.dao.InscriptionDao;
import com.hrizzon2.demotest.inscription.dto.InscriptionAttenteDto;
import com.hrizzon2.demotest.inscription.model.Inscription;
import com.hrizzon2.demotest.inscription.model.enums.StatutInscription;
import com.hrizzon2.demotest.user.dao.IntervenantDao;
import com.hrizzon2.demotest.user.dao.StagiaireDao;
import com.hrizzon2.demotest.user.dto.AdminDashboardDto;
import com.hrizzon2.demotest.user.dto.KpiDataDto;
import com.hrizzon2.demotest.user.dto.StagiaireDashboardDto;
import com.hrizzon2.demotest.user.model.Stagiaire;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DashboardService {

    @Autowired
    private StagiaireDao stagiaireDao;

    @Autowired
    private FormationDao formationDao;

    @Autowired
    private IntervenantDao intervenantDao;

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private InscriptionDao inscriptionDao;

    @Autowired
    private StatutDocumentDao statutDocumentDao;

    public AdminDashboardDto getAdminDashboardStats() {
        AdminDashboardDto dto = new AdminDashboardDto();

        // Récupérer ET extraire l'entité StatutDocument
        StatutDocument statutEnAttente = statutDocumentDao.findByNom("EN_ATTENTE")
                .orElseThrow(() -> new RuntimeException("Statut EN_ATTENTE non trouvé en base"));

        // 1. Compteurs principaux
        dto.setNbStagiaires((int) stagiaireDao.count());
        dto.setNbFormations((int) formationDao.count());
        dto.setNbIntervenants((int) intervenantDao.count());

        // 2. Nombre de documents en attente - MAINTENANT ça marche
        dto.setNbDocsAttente(documentDao.countByStatut(statutEnAttente));

        // 3. Liste des inscriptions en attente
        List<InscriptionAttenteDto> inscriptionsEnAttente = inscriptionDao
                .findByStatut(StatutInscription.valueOf("EN_ATTENTE"))
                .stream()
                .map(inscription -> {
                    Stagiaire stagiaire = inscription.getStagiaire();
                    InscriptionAttenteDto att = new InscriptionAttenteDto();
                    att.setNomStagiaire(stagiaire.getFirstName() + " " + stagiaire.getLastName());
                    att.setFormationNom(inscription.getFormation().getNom());
                    att.setStatutDossier(inscription.getStatut().toString());
                    return att;
                })
                .collect(Collectors.toList());
        dto.setInscriptionsEnAttente(inscriptionsEnAttente);

        // 4. Liste des documents à vérifier
        List<DocumentAttenteDto> docsAttente = documentDao
                .findByStatut(statutEnAttente)// doit retourner List<Document>
                .stream()
                .map(document -> {
                    DocumentAttenteDto doc = new DocumentAttenteDto();
                    doc.setNomFichier(document.getNomFichier());
                    doc.setNomStagiaire(document.getStagiaire().getFirstName() + " " + document.getStagiaire().getLastName());
                    doc.setUrlFichier(document.getUrlFichier());
                    return doc;
                })
                .collect(Collectors.toList());
        dto.setDocsAttente(docsAttente);

        return dto;
    }

    public StagiaireDashboardDto getStagiaireDashboardStats(Long idStagiaire) {
        StagiaireDashboardDto dto = new StagiaireDashboardDto();
        // Remplis avec les infos personnalisées pour le stagiaire
        return dto;
    }

    // ===== NOUVELLES MÉTHODES POUR LE FRONTEND =====

    /**
     * Calcule les KPIs pour le dashboard
     */
    public KpiDataDto calculerKpis() {
        KpiDataDto kpis = new KpiDataDto();

        // Récupérer les statuts
        StatutDocument statutEnAttente = statutDocumentDao.findByNom("EN_ATTENTE")
                .orElseThrow(() -> new RuntimeException("Statut EN_ATTENTE non trouvé"));
        StatutDocument statutEnCours = statutDocumentDao.findByNom("EN_COURS")
                .orElse(null);

        // Calculer les KPIs
        kpis.setNbStagiaires(stagiaireDao.count());
        kpis.setNbFormations(formationDao.count());
        kpis.setNbIntervenants(intervenantDao.count());
        kpis.setNbDocsAttente((long) documentDao.countByStatut(statutEnAttente));

        if (statutEnCours != null) {
            kpis.setNbDocsValidation((long) documentDao.countByStatut(statutEnCours));
        } else {
            kpis.setNbDocsValidation(0L);
        }

        kpis.setNbInscriptionsAttente((long) inscriptionDao.findByStatut(StatutInscription.EN_ATTENTE).size());

        // TODO: Calculer les évolutions si nécessaire
        kpis.setEvolutionStagiaires(0.0);
        kpis.setEvolutionFormations(0.0);

        return kpis;
    }

    /**
     * Récupère les inscriptions en attente avec limite
     */
    public List<InscriptionAttenteDto> getInscriptionsEnAttente(int limit) {
        List<Inscription> inscriptions = inscriptionDao.findByStatut(StatutInscription.EN_ATTENTE);

        return inscriptions.stream()
                .limit(limit)
                .map(inscription -> {
                    InscriptionAttenteDto dto = new InscriptionAttenteDto();

                    // Remplir toutes les propriétés nécessaires
                    dto.setId(inscription.getId());
                    dto.setStagiaireId(inscription.getStagiaire().getId());
                    dto.setNomStagiaire(inscription.getStagiaire().getLastName());
                    dto.setPrenomStagiaire(inscription.getStagiaire().getFirstName());
                    dto.setStagiaireEmail(inscription.getStagiaire().getEmail());
                    dto.setFormationId(inscription.getFormation().getId());
                    dto.setFormationNom(inscription.getFormation().getNom());
                    dto.setStatutDossier(inscription.getStatut().toString());
                    dto.setDateInscription(inscription.getDateInscription().atStartOfDay());

                    // Documents manquants - logique simplifiée
                    dto.setDocumentsManquants(List.of("CV", "Pièce d'identité")); // Exemple
                    dto.setDocumentsDeposes(2); // Exemple
                    dto.setDocumentsRequis(5);  // Exemple
                    dto.setPriorite("NORMALE"); // Exemple

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère les documents en attente avec limite
     */
    public List<DocumentAttenteDto> getDocumentsEnAttente(int limit) {
        StatutDocument statutEnAttente = statutDocumentDao.findByNom("EN_ATTENTE")
                .orElseThrow(() -> new RuntimeException("Statut EN_ATTENTE non trouvé"));

        List<Document> documents = documentDao.findByStatut(statutEnAttente);

        return documents.stream()
                .limit(limit)
                .map(document -> {
                    DocumentAttenteDto dto = new DocumentAttenteDto();

                    // Remplir toutes les propriétés nécessaires
                    dto.setId(document.getId());
                    dto.setNomFichier(document.getNomFichier());
                    dto.setNomFichierOriginal(document.getNomFichier()); // Si vous n'avez pas cette propriété
                    dto.setTypeFichier(getExtensionFichier(document.getNomFichier()));

                    if (document.getType() != null) {
                        dto.setTypeDocument(document.getType().toString());
                    } else {
                        dto.setTypeDocument("AUTRE");
                    }

                    dto.setStagiaireId(document.getStagiaire().getId());
                    dto.setNomStagiaire(document.getStagiaire().getLastName());
                    dto.setPrenomStagiaire(document.getStagiaire().getFirstName());
                    dto.setStagiaireEmail(document.getStagiaire().getEmail());
                    dto.setDateDepot(document.getDateDepot());
                    dto.setStatut(document.getStatut().getNom());
                    dto.setTaille(1024L);
                    dto.setUrlFichier(document.getUrlFichier());
                    dto.setCheminFichier(document.getUrlFichier());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Télécharge un document
     */
    public Resource telechargerDocument(Long documentId) {
        Document document = documentDao.findById(documentId.intValue())
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        try {
            // Supposons que urlFichier contient le chemin vers le fichier
            Path filePath = Paths.get(document.getUrlFichier());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Fichier non accessible: " + document.getUrlFichier());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier", e);
        }
    }

    /**
     * Récupère le nom du fichier pour téléchargement
     */
    public String getNomFichierDocument(Long documentId) {
        Document document = documentDao.findById(documentId.intValue())
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));
        return document.getNomFichier();
    }

    /**
     * Valide un document
     */
    @Transactional
    public void validerDocument(Long documentId, DocumentValidationRequestDto request, String validateur) {
        Document document = documentDao.findById(documentId.intValue())
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        StatutDocument statutValide = statutDocumentDao.findByNom("VALIDE")
                .orElseThrow(() -> new RuntimeException("Statut VALIDE non trouvé"));

        document.setStatut(statutValide);
        document.setDateDepot(LocalDateTime.now());

        documentDao.save(document);
    }

    /**
     * Rejette un document
     */
    @Transactional
    public void rejeterDocument(Long documentId, String motif, String commentaires, String validateur) {
        Document document = documentDao.findById(documentId.intValue())
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        StatutDocument statutRejete = statutDocumentDao.findByNom("REJETE")
                .orElseThrow(() -> new RuntimeException("Statut REJETE non trouvé"));

        document.setStatut(statutRejete);
        document.setCommentaire(motif);
        document.setDateDepot(LocalDateTime.now());

        documentDao.save(document);
    }

    /**
     * Méthode utilitaire pour extraire l'extension d'un fichier
     */
    private String getExtensionFichier(String nomFichier) {
        if (nomFichier != null && nomFichier.contains(".")) {
            return nomFichier.substring(nomFichier.lastIndexOf("."));
        }
        return "";
    }
}

// PAS OBLIGÉ de créer un contrôleur spécifique pour Intervenant
// si je passe par un service qui va requêter directement le nombre d’intervenants pour le dashboard
// (genre dans DashboardService).