package com.hrizzon2.demotest.document.service;

import com.hrizzon2.demotest.document.dao.DocumentDao;
import com.hrizzon2.demotest.document.dao.StatutDocumentDao;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.StatutDocument;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.document.util.TypeDocumentValidator;
import com.hrizzon2.demotest.evenement.service.EvenementService;
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

    @Autowired
    public DocumentManagementService(
            DocumentDao documentDao,
            DossierService dossierService,
            EvenementService evenementService,
            NotificationService notificationService,
            StatutDocumentDao statutDocumentDao,
            DocumentStorageService documentStorageService,
            StagiaireDao stagiaireDao,
            TypeDocumentValidator typeDocumentValidator
    ) {
        this.documentDao = documentDao;
        this.dossierService = dossierService;
        this.evenementService = evenementService;
        this.notificationService = notificationService;
        this.statutDocumentDao = statutDocumentDao;
        this.documentStorageService = documentStorageService;
        this.stagiaireDao = stagiaireDao;
        this.typeDocumentValidator = typeDocumentValidator;
    }

    @Transactional
    public Document uploadDocument(Integer stagiaireId, MultipartFile fichier, TypeDocument type) throws IOException {

        Stagiaire stagiaire = stagiaireDao.findById(stagiaireId)
                .orElseThrow(() -> new IllegalArgumentException("Stagiaire introuvable"));

        // Valider le type
        if (!typeDocumentValidator.isTypeAutorise(type)) {
            throw new IllegalArgumentException("Type de document non autorisé");
        }

        // Vérifier s’il existe déjà un document de ce type non rejeté
        boolean dejaSoumis = documentDao
                .findByDossierStagiaireIdAndType(stagiaireId, type)
                .stream()
                .anyMatch(doc -> !doc.getStatut().getNom().equals("REJETÉ"));

        if (dejaSoumis) {
            throw new IllegalArgumentException("Un document de ce type est déjà soumis.");
        }

        // Stockage MongoDB (GridFS)
        String fileId = documentStorageService.saveFile(fichier);

        // Création du document
        Document document = new Document();
        document.setStagiaire(stagiaire);
        document.setNomFichier(fichier.getOriginalFilename());
        document.setType(type);
        document.setStatut(getStatut("EN_ATTENTE"));
        document.setDateDepot(LocalDateTime.now());
        document.setUrlFichier(fileId);  // <- Utilisation de GridFS

        // Lier à un dossier
        dossierService.creerOuAssocierDossier(document, stagiaireId);

        return documentDao.save(document);
    }

    @Transactional
    public Document validerDocument(Integer documentId) {
        Document document = getDocumentEnAttente(documentId);
        document.setStatut(getStatut("VALIDÉ"));

        if (document.getDossier() != null)
            dossierService.verifierEtMettreAJourStatut(document.getDossier().getId());

        if (document.getEvenement() != null)
            evenementService.marquerJustifie(document.getEvenement().getId());

        notificationService.notifyStagiaireValidationDocument(document.getStagiaire().getId(), document.getId(), true);

        return documentDao.save(document);
    }

    @Transactional
    public Document rejeterDocument(Integer documentId) {
        Document document = getDocumentEnAttente(documentId);
        document.setStatut(getStatut("REJETÉ"));

        if (document.getDossier() != null)
            dossierService.verifierEtMettreAJourStatut(document.getDossier().getId());

        if (document.getEvenement() != null)
            evenementService.marquerNonJustifie(document.getEvenement().getId());

        notificationService.notifyStagiaireValidationDocument(document.getStagiaire().getId(), document.getId(), false);

        return documentDao.save(document);
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

}