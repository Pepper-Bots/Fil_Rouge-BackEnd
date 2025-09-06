package com.hrizzon2.demotest.document.service;

import com.hrizzon2.demotest.document.dao.DocumentDao;
import com.hrizzon2.demotest.document.dao.DocumentMongoDao;
import com.hrizzon2.demotest.document.dao.StatutDocumentDao;
import com.hrizzon2.demotest.document.dto.DocumentSummaryDto;
import com.hrizzon2.demotest.document.model.AuditAction;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.StatutDocument;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.document.util.TypeDocumentValidator;
import com.hrizzon2.demotest.evenement.service.EvenementService;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.formation.model.ListeDocumentsObligatoires;
import com.hrizzon2.demotest.formation.service.ListeDocumentsObligatoiresService;
import com.hrizzon2.demotest.inscription.service.DossierService;
import com.hrizzon2.demotest.notification.service.NotificationService;
import com.hrizzon2.demotest.user.dao.StagiaireDao;
import com.hrizzon2.demotest.user.model.Stagiaire;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DocumentManagementService {

    private final DocumentDao documentDao;
    private final DossierService dossierService;
    private final EvenementService evenementService;
    private final NotificationService notificationService;
    private final StatutDocumentDao statutDocumentDao;
    private final DocumentStorageService documentStorageService;
    private final StagiaireDao stagiaireDao;
    private final TypeDocumentValidator typeDocumentValidator;
    private final DocumentMongoDao documentMongoDao;
    private final ListeDocumentsObligatoiresService listeDocumentsObligatoiresService;

    @Autowired
    public DocumentManagementService(
            DocumentDao documentDao,
            DossierService dossierService,
            EvenementService evenementService,
            NotificationService notificationService,
            StatutDocumentDao statutDocumentDao,
            DocumentStorageService documentStorageService,
            StagiaireDao stagiaireDao,
            TypeDocumentValidator typeDocumentValidator,
            DocumentMongoDao documentMongoDao,
            ListeDocumentsObligatoiresService listeDocumentsObligatoiresService) {
        this.documentDao = documentDao;
        this.dossierService = dossierService;
        this.evenementService = evenementService;
        this.notificationService = notificationService;
        this.statutDocumentDao = statutDocumentDao;
        this.documentStorageService = documentStorageService;
        this.stagiaireDao = stagiaireDao;
        this.typeDocumentValidator = typeDocumentValidator;
        this.documentMongoDao = documentMongoDao;
        this.listeDocumentsObligatoiresService = listeDocumentsObligatoiresService;
    }


    @Transactional
    public Document uploadDocument(
            Integer stagiaireId,
            MultipartFile fichier,
            TypeDocument type,
            Formation formation) throws IOException {

        // 1. Validation du stagiaire
        Stagiaire stagiaire = stagiaireDao.findById(stagiaireId)
                .orElseThrow(() -> new IllegalArgumentException("Stagiaire introuvable"));

        // 2. Valider le type de document pour cette formation
        if (!typeDocumentValidator.isTypeAutorise(formation, type)) {
            throw new IllegalArgumentException("Type de document non autorisé pour cette formation");
        }

        // 3. Vérifier s'il existe déjà un document de ce type non rejeté
        boolean dejaSoumis = documentDao
                .findByDossierStagiaireIdAndTypeDocument(stagiaireId, type)
                .stream()
                .anyMatch(doc -> !doc.getStatut().getNom().equals("REJETÉ"));

        if (dejaSoumis) {
            throw new IllegalArgumentException("Un document de ce type est déjà soumis.");
        }

        // 4. Stockage avec synchronisation MongoDB (GridFS + DocumentMongo)
        String fileId = documentStorageService.saveFile(fichier, stagiaireId.toString(),
                stagiaire.getLastName());

        // 5. Création Document MySQL
        Document document = new Document();
        document.setStagiaire(stagiaire);
        document.setNomFichier(fichier.getOriginalFilename());
        document.setTypeDocument(type);
        document.setStatut(getStatut("EN_ATTENTE"));
        document.setDateDepot(LocalDateTime.now());
        document.setUrlFichier(fileId);
        document.setFormation(formation);

        // 6. Lier à un dossier
        dossierService.creerOuAssocierDossier(document, stagiaireId);

        // 7. Sauvegarder dans MySQL
        Document savedDoc = documentDao.save(document);

        // 8. Mise à jour DocumentMongo avec l'ID MySQL et informations supplémentaires
        updateDocumentMongo(fileId, savedDoc);

        // 9. Audit de l'upload
        addAuditAction(fileId, "UPLOAD", stagiaire.getLastName());

        return savedDoc;
    }

    // Méthode principale (avec MongoDB + audit)
    @Transactional
    public Document validerDocument(Integer documentId, String commentaire) {
        Document document = getDocumentEnAttente(documentId);
        document.setStatut(getStatut("VALIDÉ"));

        // Ajout commentaire
        if (commentaire != null && !commentaire.trim().isEmpty()) {
            document.setCommentaire(commentaire);
        }

        // Synchronisation MongoDB
        updateDocumentMongoStatus(document.getUrlFichier(), "VALIDÉ");

        // Audit dans MongoDB
        addAuditAction(document.getUrlFichier(), "VALIDATION", "Système");

        // Logique métier (recalcul dossier, notifications...)
        if (document.getDossier() != null)
            dossierService.verifierEtMettreAJourStatut(document.getDossier().getId());

        if (document.getEvenement() != null)
            evenementService.marquerJustifie(document.getEvenement().getId());

        notificationService.notifyStagiaireValidationDocument(
                document.getStagiaire().getId(), document.getId(), true);

        return documentDao.save(document);
    }

    @Transactional
    public Document rejeterDocument(Integer documentId) {
        Document document = getDocumentEnAttente(documentId);
        document.setStatut(getStatut("REJETÉ"));

        // Synchronisation MongoDB
        updateDocumentMongoStatus(document.getUrlFichier(), "REJETÉ");

        // Logique métier
        if (document.getDossier() != null)
            dossierService.verifierEtMettreAJourStatut(document.getDossier().getId());

        if (document.getEvenement() != null)
            evenementService.marquerNonJustifie(document.getEvenement().getId());

        // Audit dans MongoDB
        addAuditAction(document.getUrlFichier(), "REJET", "Système");

        // Notification
        notificationService.notifyStagiaireValidationDocument(document.getStagiaire().getId(), document.getId(), false);

        return documentDao.save(document);
    }


    private void updateDocumentMongo(String fileId, Document document) {
        documentMongoDao.findById(fileId).ifPresent(docMongo -> {
            docMongo.setType(document.getTypeDocument().toString());
            docMongo.setStatut(document.getStatut().getNom());
            documentMongoDao.save(docMongo);
        });
    }

    private void updateDocumentMongoStatus(String fileId, String newStatus) {
        documentMongoDao.findById(fileId).ifPresent(docMongo -> {
            docMongo.setStatut(newStatus);
            documentMongoDao.save(docMongo);
        });
    }

    private void addAuditAction(String fileId, String action, String par) {
        documentMongoDao.findById(fileId).ifPresent(docMongo -> {
            if (docMongo.getAudit() == null) {
                docMongo.setAudit(new ArrayList<>());
            }

            AuditAction auditAction = new AuditAction();
            auditAction.setAction(action);
            auditAction.setDate(new Date());
            auditAction.setPar(par);

            docMongo.getAudit().add(auditAction);
            documentMongoDao.save(docMongo);
        });
    }

    private Document getDocumentEnAttente(Integer id) {
        Document doc = documentDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document non trouvé"));
        if (!"EN_ATTENTE".equals(doc.getStatut().getNom()))
            throw new IllegalStateException("Le document n'est pas en attente");
        return doc;
    }

    private StatutDocument getStatut(String nom) {
        return statutDocumentDao.findByNom(nom)
                .orElseThrow(() -> new IllegalStateException("Statut " + nom + " introuvable"));
    }


    /**
     * Méthode ajoutée : récupérer documents par stagiaire + formation
     * REMPLACE : PieceJointeStagiaireDao.findByStagiaireIdAndFormationId()
     */
    public List<Document> getDocumentsByStagiaireAndFormation(Integer stagiaireId, Integer formationId) {
        // Utiliser votre DocumentDao existant
        return documentDao.findByStagiaireIdAndFormationId(stagiaireId, formationId);
        // Note: il faudra ajouter cette méthode à DocumentDao
    }

    /**
     * Méthode ajoutée : calculer statut dossier
     * REMPLACE : PieceJointeStagiaireService.getStatutDocumentsDossier()
     */
    public List<DocumentSummaryDto> getStatutDocumentsDossier(Stagiaire stagiaire, Formation formation) {
        // Reprendre la logique de PieceJointeStagiaireService mais avec Document
        List<ListeDocumentsObligatoires> attendus = listeDocumentsObligatoiresService.findByFormation(formation);
        List<Document> transmis = getDocumentsByStagiaireAndFormation(stagiaire.getId(), formation.getId());

        List<DocumentSummaryDto> result = new ArrayList<>();
        for (ListeDocumentsObligatoires attendu : attendus) {
            DocumentSummaryDto dto = new DocumentSummaryDto();
            dto.setTypeDocument(attendu.getTypeDocument());
            dto.setObligatoire(true);

            Document docTransmis = transmis.stream()
                    .filter(doc -> doc.getTypeDocument() == attendu.getTypeDocument())
                    .findFirst()
                    .orElse(null);

            dto.setTransmis(docTransmis != null);
            if (docTransmis != null) {
                dto.setFichier(docTransmis.getNomFichier());
                dto.setStatut(docTransmis.getStatut().getNom());
            } else {
                dto.setFichier(null);
                dto.setStatut(null);
            }
            result.add(dto);
        }
        return result;
    }

    /**
     * Méthode ajoutée : vérifier complétude dossier
     * REMPLACE : PieceJointeStagiaireService.isDossierComplet()
     */
    public boolean isDossierComplet(Stagiaire stagiaire, Formation formation) {
        List<DocumentSummaryDto> docs = getStatutDocumentsDossier(stagiaire, formation);
        return docs.stream().allMatch(dto ->
                dto.isTransmis() && "VALIDÉ".equalsIgnoreCase(dto.getStatut())
        );
    }

    @Transactional
    public Document rejeterDocumentWithComment(Integer documentId, String commentaire) {
        Document document = getDocumentEnAttente(documentId);
        document.setStatut(getStatut("REJETÉ"));
        document.setCommentaire(commentaire);

        updateDocumentMongoStatus(document.getUrlFichier(), "REJETÉ");
        addAuditAction(document.getUrlFichier(), "REJET_AVEC_COMMENTAIRE", "Admin");

        if (document.getDossier() != null)
            dossierService.verifierEtMettreAJourStatut(document.getDossier().getId());

        notificationService.notifyStagiaireValidationDocument(
                document.getStagiaire().getId(), document.getId(), false);

        return documentDao.save(document);
    }
}