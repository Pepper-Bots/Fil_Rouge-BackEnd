//package com.hrizzon2.demotest.document.service;
//
//import com.hrizzon2.demotest.document.dao.DocumentDao;
//import com.hrizzon2.demotest.document.dao.StatutDocumentDao;
//import com.hrizzon2.demotest.document.model.Document;
//import com.hrizzon2.demotest.document.model.StatutDocument;
//import com.hrizzon2.demotest.evenement.service.EvenementService;
//import com.hrizzon2.demotest.inscription.service.DossierService;
//import com.hrizzon2.demotest.notification.service.NotificationService;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
/// **
// * Service de validation des documents soumis par les stagiaires.
// *
// * <p>Ce service gère le processus de validation et de rejet des documents,
// * en coordonnant les mises à jour des statuts dans les dossiers et événements
// * associés, ainsi que l'envoi de notifications aux stagiaires.</p>
// *
// * <p>Fonctionnalités principales :</p>
// * <ul>
// *   <li>Validation de documents avec mise à jour automatique des statuts</li>
// *   <li>Rejet de documents avec gestion des commentaires</li>
// *   <li>Synchronisation avec les dossiers d'inscription</li>
// *   <li>Gestion des justificatifs d'événements (absences/retards)</li>
// *   <li>Notifications automatiques aux stagiaires</li>
// * </ul>
// *
// * <p>Ce service est transactionnel et assure la cohérence des données
// * lors des opérations de validation/rejet.</p>
// *
// * @author Votre nom
// * @version 1.0
// * @since 1.0
// */
//@Service
//public class ValidationDocumentService {
//
//    private final DocumentDao documentDao;
//    private final DossierService dossierService;
//    private final EvenementService evenementService;
//    private final NotificationService notificationService;
//    private final StatutDocumentDao statutDocumentDao;
//
//
//    @Autowired
//    public ValidationDocumentService(DocumentDao documentDao,
//                                     DossierService dossierService,
//                                     EvenementService evenementService,
//                                     NotificationService notificationService,
//                                     StatutDocumentDao statutDocumentDao) {
//        this.documentDao = documentDao;
//        this.dossierService = dossierService;
//        this.evenementService = evenementService;
//        this.notificationService = notificationService;
//        this.statutDocumentDao = statutDocumentDao;
//    }
//
//    /**
//     * Valide un document en attente et met à jour tous les éléments associés.
//     *
//     * <p>Cette méthode effectue les opérations suivantes :</p>
//     * <ol>
//     *   <li>Vérifie que le document existe et est en statut "EN_ATTENTE"</li>
//     *   <li>Change le statut du document vers "VALIDÉ"</li>
//     *   <li>Met à jour le statut du dossier associé si applicable</li>
//     *   <li>Marque l'événement comme justifié si applicable</li>
//     *   <li>Envoie une notification de validation au stagiaire</li>
//     * </ol>
//     *
//     * @param documentId l'identifiant unique du document à valider
//     * @return le document validé avec son nouveau statut
//     * @throws RuntimeException      si le document n'existe pas
//     * @throws RuntimeException      si le statut "VALIDÉ" n'est pas trouvé en base
//     * @throws IllegalStateException si le document n'est pas en attente
//     */
//    @Transactional
//    public Document validerDocument(Long documentId) {
//        Document document = documentDao.findById(Math.toIntExact(documentId))
//                .orElseThrow(() -> new RuntimeException("Document non trouvé"));
//
//        // Récupérer l'entité statut "VALIDÉ" depuis la base
//        StatutDocument statutValide = statutDocumentDao.findByNom("VALIDÉ")
//                .orElseThrow(() -> new RuntimeException("Statut VALIDÉ non trouvé"));
//
//        document.setStatut(statutValide);
//
//        // Mise à jour dossier si document lié à un dossier
//        if (document.getDossier() != null) {
//            dossierService.verifierEtMettreAJourStatut(document.getDossier().getId());
//        }
//
//        // Mise à jour évènement si document lié à un évènement
//        if (document.getEvenement() != null) {
//            evenementService.marquerJustifie(document.getEvenement().getId());
//        }
//
//        Document sauvegarde = documentDao.save(document);
//
//        notificationService.notifyStagiaireValidationDocument(document.getStagiaire().getId(), document.getId(), true);
//
//        return sauvegarde;
//    }
//
//    /**
//     * Rejette un document en attente et met à jour tous les éléments associés.
//     *
//     * <p>Cette méthode effectue les opérations suivantes :</p>
//     * <ol>
//     *   <li>Vérifie que le document existe et est en statut "EN_ATTENTE"</li>
//     *   <li>Change le statut du document vers "REJETÉ"</li>
//     *   <li>Met à jour le statut du dossier associé si applicable</li>
//     *   <li>Marque l'événement comme non justifié si applicable</li>
//     *   <li>Envoie une notification de rejet au stagiaire</li>
//     * </ol>
//     *
//     * @param documentId l'identifiant unique du document à rejetér
//     * @return le document rejeté avec son nouveau statut
//     * @throws RuntimeException      si le document n'existe pas
//     * @throws RuntimeException      si le statut "REJETÉ" n'est pas trouvé en base
//     * @throws IllegalStateException si le document n'est pas en attente
//     */
//    @Transactional
//    public Document rejetérDocument(Long documentId) {
//        Document document = documentDao.findById(Math.toIntExact(documentId))
//                .orElseThrow(() -> new RuntimeException("Document non trouvé"));
//
//        // Récupérer l'entité statut "REJETÉ" depuis la base
//        StatutDocument statutrejete = statutDocumentDao.findByNom("REJETÉ")
//                .orElseThrow(() -> new RuntimeException("Statut REJETÉ non trouvé"));
//
//        document.setStatut(statutrejete);
//
//        if (document.getDossier() != null) {
//            dossierService.verifierEtMettreAJourStatut(document.getDossier().getId());
//        }
//
//        if (document.getEvenement() != null) {
//            evenementService.marquerNonJustifie(document.getEvenement().getId());
//        }
//
//        Document sauvegarde = documentDao.save(document);
//
//        notificationService.notifyStagiaireValidationDocument(document.getStagiaire().getId(), document.getId(), false);
//
//        return sauvegarde;
//    }
//
//}
//
/// / TODO
/// /  - validerDocument(documentId), rejeterDocument(documentId)
/// /  Créer / Conserver
/// /  Changement de statut, mise à jour dossier & évènement + notif