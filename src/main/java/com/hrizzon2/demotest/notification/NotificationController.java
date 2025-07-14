package com.hrizzon2.demotest.notification;

import com.hrizzon2.demotest.model.enums.NiveauDroit;
import com.hrizzon2.demotest.model.enums.TypeNotification;
import com.hrizzon2.demotest.user.model.enums.TypeAdmin;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST exposant les endpoints liés à la gestion des notifications.
 *
 * <p>Permet d'envoyer des notifications à des utilisateurs ou groupes d'utilisateurs
 * selon leur rôle et de gérer leur statut.</p>
 *
 * <p>Autorise le Cross-Origin depuis l'adresse front-end http://localhost:4200.</p>
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;


    /**
     * Constructeur avec injection de dépendance du service {@link NotificationService}.
     *
     * @param notificationService service métier de gestion des notifications
     */
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Envoie une notification à tous les administrateurs d'un type et niveau de droit donné.
     * Exemple d'URL : /notifications/send-to-admins?type=RÉFÉRENT&niveau=GESTION&notifType=INFORMATION
     *
     * @param type      type d'administrateur ciblé
     * @param niveau    niveau de droit des administrateurs ciblés
     * @param notifType type de notification à envoyer
     */
    @PostMapping("/send-to-admins")
    public void sendNotificationToAdmins(
            @RequestParam TypeAdmin type,
            @RequestParam NiveauDroit niveau,
            @RequestParam TypeNotification notifType) {

        notificationService.notifyAdmins(type, niveau, notifType);
    }
    //Todo -> quel type de notif et dans quel but ?

    /**
     * Envoie une notification à une liste d'utilisateurs identifiés par leurs IDs.
     *
     * @param notifType type de notification à envoyer
     * @param userIds   liste des IDs des utilisateurs destinataires
     */
    @PostMapping("/send-to-users")
    public void sendNotificationToUsers(
            @RequestParam TypeNotification notifType,
            @RequestBody List<Integer> userIds) {
        notificationService.sendNotificationToUsers(notifType, userIds);
    }
    // todo notif envoyée par qui et pour quoi ?

    /**
     * Récupère la liste de toutes les notifications d'un utilisateur donné.
     *
     * @param userId identifiant de l'utilisateur
     * @return liste des notifications reçues par l'utilisateur
     */
    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Integer userId) {
        return notificationService.getNotificationsForUser(userId);
    }
    // todo -> permettre affichage dans le front

    /**
     * Marque une notification comme lue.
     *
     * @param notificationId identifiant de la notification à marquer comme lue
     */
    @PostMapping("/mark-as-read/{notificationId}")
    public void markNotificationAsRead(@PathVariable int notificationId) {
        notificationService.markNotificationAsRead(notificationId);
    }

    /**
     * Envoie une notification aux administrateurs lorsqu'un stagiaire soumet un document.
     *
     * @param stagiaireId identifiant du stagiaire ayant soumis un document
     */
    @PostMapping("/send/document-submission")
    public void sendAdminOnDocumentSubmission(@RequestParam int stagiaireId) {
        notificationService.notifyAdminOnDocumentSubmission(stagiaireId);
    }

    /**
     * Envoie une notification au stagiaire lors de la validation d’un document.
     *
     * @param stagiaireId identifiant du stagiaire concerné
     */
    @PostMapping("/send/document-validated")
    public void sendNotificationDocumentValidated(@RequestParam int stagiaireId) {
        notificationService.notifyStagiaireOnDocumentValidated(stagiaireId);
    }

    /**
     * Envoie un avertissement au stagiaire si un document n'a pas été validé.
     *
     * @param stagiaireId identifiant du stagiaire concerné
     */
    @PostMapping("/send/document-not-validated")
    public void sendWarningDocumentToStagiaire(@RequestParam int stagiaireId) {
        notificationService.notifyStagiaireOnDocumentNotValidated(stagiaireId);
    }

    /**
     * Envoie un avertissement au stagiaire si un seuil de retard est proche ou atteint.
     *
     * @param stagiaireId identifiant du stagiaire concerné
     */
    @PostMapping("/send/delay-warning")
    public void sendWarningDelayToStagiaire(@RequestParam int stagiaireId) {
        notificationService.notifyStagiaireOnDelayThreshold(stagiaireId);
    }

    /**
     * Envoie un avertissement au stagiaire si un seuil d'absence est atteint.
     *
     * @param stagiaireId identifiant du stagiaire concerné
     */
    @PostMapping("/send/absence-warning")
    public void sendWarningAbsenceToStagiaire(@RequestParam int stagiaireId) {
        notificationService.notifyStagiaireOnAbsenceThreshold(stagiaireId);
    }

    /**
     * Envoie un rappel au stagiaire pour qu'il complète son dossier.
     *
     * @param stagiaireId identifiant du stagiaire concerné
     */
    @PostMapping("/send/reminder")
    public void sendReminderToStagiaire(@RequestParam int stagiaireId) {
        notificationService.remindStagiaireToCompleteFile(stagiaireId);
    }
    // todo déterminer le moment du rappel


    // TODO : ajouter dans le front la gestion de la lecture des notifications et affichage contextualisé

}

// todo -> déterminer quand les notifications pour les seuils proches ou atteints des retard ou absence sont envoyées,
//  qu'est ce qui les déclenche ?