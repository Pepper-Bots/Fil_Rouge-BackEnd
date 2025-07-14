package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.document.dao.DocumentDao;
import com.hrizzon2.demotest.document.dao.StatutDocumentDao;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.StatutDocument;
import com.hrizzon2.demotest.notification.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationDocumentService {

    private final DocumentDao documentDao;
    private final DossierService dossierService;
    private final EvenementService evenementService;
    private final NotificationService notificationService;
    private final StatutDocumentDao statutDocumentDao;


    @Autowired
    public ValidationDocumentService(DocumentDao documentDao,
                                     DossierService dossierService,
                                     EvenementService evenementService,
                                     NotificationService notificationService,
                                     StatutDocumentDao statutDocumentDao) {
        this.documentDao = documentDao;
        this.dossierService = dossierService;
        this.evenementService = evenementService;
        this.notificationService = notificationService;
        this.statutDocumentDao = statutDocumentDao;
    }

    @Transactional
    public Document validerDocument(Long documentId) {
        Document document = documentDao.findById(Math.toIntExact(documentId))
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        // Récupérer l'entité statut "VALIDÉ" depuis la base
        StatutDocument statutValide = statutDocumentDao.findByNom("VALIDÉ")
                .orElseThrow(() -> new RuntimeException("Statut VALIDÉ non trouvé"));

        document.setStatut(statutValide);

        // Mise à jour dossier si document lié à un dossier
        if (document.getDossier() != null) {
            dossierService.verifierEtMettreAJourStatut(document.getDossier().getId());
        }

        // Mise à jour évènement si document lié à un évènement
        if (document.getEvenement() != null) {
            evenementService.marquerJustifie(document.getEvenement().getId());
        }

        Document sauvegarde = documentDao.save(document);

        notificationService.notifyStagiaireValidationDocument(document.getStagiaire().getId(), document.getId(), true);

        return sauvegarde;
    }

    @Transactional
    public Document rejeterDocument(Long documentId) {
        Document document = documentDao.findById(Math.toIntExact(documentId))
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        // Récupérer l'entité statut "REJETÉ" depuis la base
        StatutDocument statutRejete = statutDocumentDao.findByNom("REJETÉ")
                .orElseThrow(() -> new RuntimeException("Statut REJETÉ non trouvé"));

        document.setStatut(statutRejete);

        if (document.getDossier() != null) {
            dossierService.verifierEtMettreAJourStatut(document.getDossier().getId());
        }

        if (document.getEvenement() != null) {
            evenementService.marquerNonJustifie(document.getEvenement().getId());
        }

        Document sauvegarde = documentDao.save(document);

        notificationService.notifyStagiaireValidationDocument(document.getStagiaire().getId(), document.getId(), false);

        return sauvegarde;
    }

}

// TODO
//  - validerDocument(documentId), rejeterDocument(documentId)
//  Créer / Conserver
//  Changement de statut, mise à jour dossier & évènement + notif