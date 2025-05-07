package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.model.Notification;
import com.hrizzon2.demotest.model.enums.NiveauDroit;
import com.hrizzon2.demotest.model.enums.TypeAdmin;
import com.hrizzon2.demotest.model.enums.TypeNotification;
import com.hrizzon2.demotest.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Envoie une notification à tous les admins selon leur rôle et leur niveau de droit.
     * Exemple : /notifications/send-to-admins?type=RÉFÉRENT&niveau=GESTION&notifType=INFORMATION
     */
    @PostMapping("/send-to-admins")
    public void sendNotificationToAdmins(
            @RequestParam TypeAdmin type,
            @RequestParam NiveauDroit niveau,
            @RequestParam TypeNotification notifType) {

        notificationService.notifyAdmins(type, niveau, notifType);
    }

    /**
     * Envoie une notification basée sur un type à plusieurs utilisateurs.
     */
    @PostMapping("/send-to-users")
    public void sendNotificationToUsers(
            @RequestParam TypeNotification notifType,
            @RequestBody List<Integer> userIds) {
        notificationService.sendNotificationToUsers(notifType, userIds);
    }

    /**
     * Récupère toutes les notifications d’un utilisateur.
     */
    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Integer userId) {
        return notificationService.getNotificationsForUser(userId);
    }

    @PostMapping("/send/document-submission")
    public void sendAdminOnDocumentSubmission(@RequestParam int stagiaireId) {
        notificationService.notifyAdminOnDocumentSubmission(stagiaireId);
    }

    @PostMapping("/send/document-not-validated")
    public void sendWarningDocumentToStagiaire(@RequestParam int stagiaireId) {
        notificationService.notifyStagiaireOnDocumentNotValidated(stagiaireId);
    }

    @PostMapping("/send/absence-warning")
    public void sendWarningAbsenceToStagiaire(@RequestParam int stagiaireId) {
        notificationService.notifyStagiaireOnAbsenceThreshold(stagiaireId);
    }

    @PostMapping("/send/reminder")
    public void sendReminderToStagiaire(@RequestParam int stagiaireId) {
        notificationService.remindStagiaireToCompleteFile(stagiaireId);
    }

    // Méthode pour marquer une notification comme lue
    @PostMapping("/mark-as-read/{notificationId}")
    public void markNotificationAsRead(@PathVariable int notificationId) {
        notificationService.markNotificationAsRead(notificationId);
    }
}
