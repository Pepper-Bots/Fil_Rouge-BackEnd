package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.dao.NotificationDao;
import com.hrizzon2.demotest.dao.NotificationTemplateDao;
import com.hrizzon2.demotest.dao.UserDao;
import com.hrizzon2.demotest.model.Admin;
import com.hrizzon2.demotest.model.Notification;
import com.hrizzon2.demotest.model.NotificationTemplate;
import com.hrizzon2.demotest.model.User;
import com.hrizzon2.demotest.model.enums.NiveauDroit;
import com.hrizzon2.demotest.model.enums.TypeAdmin;
import com.hrizzon2.demotest.model.enums.TypeNotification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

//  création, lecture, mise à jour des notifications (CRUD).

@Service
public class NotificationService {

    private final NotificationDao notificationDao;
    private final NotificationTemplateDao templateDao;
    private final UserDao userDao;
    private final AdminService adminService;

    public NotificationService(NotificationDao notificationDao,
                               NotificationTemplateDao templateDao,
                               UserDao userDao, AdminService adminService) {
        this.notificationDao = notificationDao;
        this.templateDao = templateDao;
        this.userDao = userDao;
        this.adminService = adminService;
    }

    // Méthode dans NotificationService pour envoyer des notifications aux utilisateurs spécifiques
    public void sendNotificationToUsers(TypeNotification notifType, List<Integer> userIds) {
        NotificationTemplate template = templateDao.findByType(notifType);

        if (template != null && !userIds.isEmpty()) {
            for (Integer userId : userIds) {
                User user = userDao.findById(userId).orElse(null);
                if (user != null) {
                    Notification notification = new Notification();
                    notification.setTemplate(template);
                    notification.setDestinataire(user);
                    notification.setRead(false);
                    notification.setDate(LocalDateTime.now());
                    notificationDao.save(notification);
                }
            }
        }
    }

    /**
     * Envoie une notification automatique aux admins lorsqu'un document est soumis.
     */
    public void notifyAdminOnDocumentSubmission(int stagiaireId) {

        // Filtrer les admins avec un rôle spécifique
        // Utiliser AdminService pour récupérer les admins filtrés par type et niveau de droit
        List<Admin> admins = adminService.getAdminsByTypeAndNiveau(TypeAdmin.RESPONSABLE_FORMATION, NiveauDroit.ADMIN);

        NotificationTemplate template = templateDao.findByType(TypeNotification.INFORMATION);

        if (template != null && !admins.isEmpty()) {
            for (Admin admin : admins) {
                Notification notification = new Notification();
                notification.setTemplate(template);
                notification.setDestinataire(admin); // Admin hérite de User
                notification.setRead(false);
                notification.setDate(LocalDateTime.now());
                notificationDao.save(notification);
            }
        }
    }

    // Méthode dans NotificationService pour envoyer des notifications aux admins
    public void notifyAdmins(TypeAdmin type, NiveauDroit niveau, TypeNotification notifType) {
        // Utilisation de adminService pour récupérer les admins filtrés par type et niveau de droit
        List<Admin> admins = adminService.getAdminsByTypeAndNiveau(type, niveau);

        NotificationTemplate template = templateDao.findByType(notifType);

        if (template != null && !admins.isEmpty()) {
            for (Admin admin : admins) {
                Notification notification = new Notification();
                notification.setTemplate(template);
                notification.setDestinataire(admin); // Admin hérite de User
                notification.setRead(false);
                notification.setDate(LocalDateTime.now());
                notificationDao.save(notification);
            }
        }
    }

    /**
     * Notifie le stagiaire qu’un document a été validé par l’administrateur.
     *
     * @param stagiaireId id du stagiaire concerné
     */
    public void notifyStagiaireOnDocumentValidated(int stagiaireId) {
        User stagiaire = userDao.findById(stagiaireId).orElse(null);
        if (stagiaire != null) {
            NotificationTemplate template = templateDao.findByType(TypeNotification.CONFIRMATION);
            if (template != null) {
                Notification notification = new Notification();
                notification.setTemplate(template);
                notification.setDestinataire(stagiaire);
                notification.setRead(false);
                notification.setDate(LocalDateTime.now());
                notificationDao.save(notification);
            }
        }
    }

    /**
     * Envoie une notification de type WARNING_DOCUMENT au stagiaire si son document n'est pas validé.
     */
    public void notifyStagiaireOnDocumentNotValidated(int stagiaireId) {
        User stagiaire = userDao.findById(stagiaireId).orElse(null);

        if (stagiaire != null) {
            NotificationTemplate template = templateDao.findByType(TypeNotification.WARNING_ITEM);
            if (template != null) {
                Notification notification = new Notification();
                notification.setTemplate(template);
                notification.setDestinataire(stagiaire);
                notification.setRead(false);
                notification.setDate(LocalDateTime.now());
                notificationDao.save(notification);
            }
        }
    }

    /**
     * Envoie une notification WARNING_ABSENCE au stagiaire lorsque son seuil d'absence est atteint ou proche
     */
    public void notifyStagiaireOnAbsenceThreshold(int stagiaireId) {
        User stagiaire = userDao.findById(stagiaireId).orElse(null);

        if (stagiaire != null) {
            NotificationTemplate template = templateDao.findByType(TypeNotification.WARNING_ABSENCE);
            if (template != null) {
                Notification notification = new Notification();
                notification.setDestinataire(stagiaire);
                notification.setRead(false);
                notification.setDate(LocalDateTime.now());
                notificationDao.save(notification);
            }
            // todo modifier pour ajouter le rapprochement du seuil (pas notif seulement pour seuil atteint)
        }
    }

    /**
     * Envoie une notification RAPPEL au stagiaire si son dossier d'inscription est incomplet.
     * Garder à jour la méthode de rappel avec une date ou contexte (TODO à définir)
     */
    public void remindStagiaireToCompleteFile(int stagiaireId) {
        User stagiaire = userDao.findById(stagiaireId).orElse(null);
        if (stagiaire != null) {
            NotificationTemplate template = templateDao.findByType(TypeNotification.RAPPEL);
            if (template != null) {
                Notification notification = new Notification();
                notification.setDestinataire(stagiaire);
                notification.setRead(false);
                notification.setDate(LocalDateTime.now());
                notificationDao.save(notification);
            }
        }
    }

    /**
     * Marque la notification comme lue.
     */
    public void markNotificationAsRead(int notificationId) {
        Notification notification = notificationDao.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notificationDao.save(notification);
        }
    }

    /**
     * Récupère toutes les notifications d’un utilisateur.
     */
    public List<Notification> getNotificationsForUser(Integer userId) {
        return notificationDao.findByDestinataireId(userId);
    }

    // TODO à vérifier
    public void notifyStagiaireValidationDocument(long id, Integer id1, boolean b) {
    }


    /**
     * Notifie le stagiaire que le seuil de retard est proche ou atteint.
     *
     * @param stagiaireId id du stagiaire concerné
     */
    public void notifyStagiaireOnDelayThreshold(int stagiaireId) {
        User stagiaire = userDao.findById(stagiaireId).orElse(null);
        if (stagiaire != null) {
            NotificationTemplate template = templateDao.findByType(TypeNotification.WARNING_DELAY);
            if (template != null) {
                Notification notification = new Notification();
                notification.setTemplate(template);
                notification.setDestinataire(stagiaire);
                notification.setRead(false);
                notification.setDate(LocalDateTime.now());
                notificationDao.save(notification);
            }
        }
    }

    /**
     * Envoie une notification personnalisée à un administrateur liée à un stagiaire et un type d'alerte.
     *
     * @param admin       admin destinataire
     * @param stagiaireId id du stagiaire concerné
     * @param type        type d'alerte ("Absence", "Retard", etc.)
     * @param seuil       message lié au seuil ("Seuil atteint", "Seuil proche")
     */
    public void sendCustomAdminNotification(Admin admin, int stagiaireId, String type, String seuilAtteint) {
        // Créer et sauvegarder notification personnalisée
    }
}

// todo
//  - notifyStagiaireValidationDocument(stagiaireId, documentId, boolean valide)
//  Conserver
//  Notifications mails et in-app sur validation / rejet documents
