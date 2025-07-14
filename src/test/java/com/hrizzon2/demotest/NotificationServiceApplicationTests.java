package com.hrizzon2.demotest;

import com.hrizzon2.demotest.mock.MockConfig;
import com.hrizzon2.demotest.notification.dao.NotificationDao;
import com.hrizzon2.demotest.notification.dao.NotificationTemplateDao;
import com.hrizzon2.demotest.notification.model.Notification;
import com.hrizzon2.demotest.notification.model.NotificationTemplate;
import com.hrizzon2.demotest.notification.model.enums.TypeNotification;
import com.hrizzon2.demotest.notification.service.NotificationService;
import com.hrizzon2.demotest.user.dao.UserDao;
import com.hrizzon2.demotest.user.model.Admin;
import com.hrizzon2.demotest.user.model.User;
import com.hrizzon2.demotest.user.model.enums.NiveauDroit;
import com.hrizzon2.demotest.user.model.enums.TypeAdmin;
import com.hrizzon2.demotest.user.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Classe de tests unitaires pour le service {@link NotificationService}.
 *
 * <p>Ces tests vérifient le bon comportement des méthodes de notification,
 * notamment la création et la sauvegarde des notifications envoyées aux utilisateurs
 * (stagiaires, administrateurs) en fonction des différents scénarios métier.</p>
 *
 * <p>J'utilise Mockito pour simuler les dépendances {@link NotificationDao}, {@link NotificationTemplateDao}
 * et {@link UserDao} afin d'isoler la logique métier du service.</p>
 *
 * <p>Les scénarios testés comprennent :</p>
 * <ul>
 *   <li>Envoi de notification lors de la validation d’un document</li>
 *   <li>Gestion du cas où l’utilisateur destinataire est introuvable</li>
 *   <li>Envoi de notification lors du rejet d’un document</li>
 *   <li>Création de notification personnalisée pour un administrateur</li>
 * </ul>
 */
@SpringBootTest
@Import(MockConfig.class)
public class NotificationServiceApplicationTests {

    @Mock
    private NotificationDao notificationDao;

    @Mock
    private NotificationTemplateDao templateDao;

    @Mock
    private UserDao userDao;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private NotificationService notificationService;

    private User mockStagiaire;
    private Admin mockAdmin;
    private NotificationTemplate mockTemplate;

    /**
     * Initialisation des mocks avant chaque test.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockStagiaire = mock(User.class);
        mockAdmin = mock(Admin.class);
        mockTemplate = mock(NotificationTemplate.class);
    }

    // TODO ?
    @Test
    void testSendNotificationToUsers_WithValidUsers() {
        List<Integer> userIds = Arrays.asList(1, 2);
        when(templateDao.findByType(TypeNotification.RAPPEL)).thenReturn(mockTemplate);
        when(userDao.findById(1)).thenReturn(Optional.of(mockStagiaire));
        when(userDao.findById(2)).thenReturn(Optional.of(mockStagiaire));

        notificationService.sendNotificationToUsers(TypeNotification.RAPPEL, userIds);

        verify(notificationDao, times(2)).save(any(Notification.class));
    }

    // todo ?
    @Test
    void testSendNotificationToUsers_WithEmptyUserIds() {
        notificationService.sendNotificationToUsers(TypeNotification.RAPPEL, Collections.emptyList());
        verify(notificationDao, never()).save(any());
    }

    // todo ?
    @Test
    void testSendNotificationToUsers_WithUserNotFound() {
        List<Integer> userIds = Arrays.asList(1);
        when(templateDao.findByType(TypeNotification.RAPPEL)).thenReturn(mockTemplate);
        when(userDao.findById(1)).thenReturn(Optional.empty());

        notificationService.sendNotificationToUsers(TypeNotification.RAPPEL, userIds);

        verify(notificationDao, never()).save(any());
    }

    // todo
    @Test
    void testNotifyAdminOnDocumentSubmission_WithAdmins() {
        List<Admin> admins = Arrays.asList(mockAdmin);
        when(adminService.getAdminsByTypeAndNiveau(any(), any())).thenReturn(admins);
        when(templateDao.findByType(TypeNotification.INFORMATION)).thenReturn(mockTemplate);

        notificationService.notifyAdminOnDocumentSubmission(1);

        verify(notificationDao, times(1)).save(any(Notification.class));
    }

    // todo
    @Test
    void testNotifyAdminOnDocumentSubmission_NoAdmins() {
        when(adminService.getAdminsByTypeAndNiveau(any(), any())).thenReturn(Collections.emptyList());
        when(templateDao.findByType(TypeNotification.INFORMATION)).thenReturn(mockTemplate);

        notificationService.notifyAdminOnDocumentSubmission(1);

        verify(notificationDao, never()).save(any());
    }

    @Test
    void testNotifyAdmins_WithAdmins() {
        List<Admin> admins = Arrays.asList(mockAdmin, mockAdmin);
        when(adminService.getAdminsByTypeAndNiveau(any(TypeAdmin.class), any(NiveauDroit.class))).thenReturn(admins);
        when(templateDao.findByType(TypeNotification.INFORMATION)).thenReturn(mockTemplate);

        notificationService.notifyAdmins(TypeAdmin.RESPONSABLE_FORMATION, NiveauDroit.ADMIN, TypeNotification.INFORMATION);

        verify(notificationDao, times(2)).save(any(Notification.class));
    }

    @Test
    void testNotifyStagiaireOnDocumentValidated_WithUser() {
        when(userDao.findById(1)).thenReturn(Optional.of(mockStagiaire));
        when(templateDao.findByType(TypeNotification.CONFIRMATION)).thenReturn(mockTemplate);

        notificationService.notifyStagiaireOnDocumentValidated(1);

        verify(notificationDao, times(1)).save(any(Notification.class));
    }

    @Test
    void testNotifyStagiaireOnDocumentValidated_NoUser() {
        when(userDao.findById(1)).thenReturn(Optional.empty());

        notificationService.notifyStagiaireOnDocumentValidated(1);

        verify(notificationDao, never()).save(any());
    }

    @Test
    void testNotifyStagiaireOnDocumentNotValidated_WithUser() {
        when(userDao.findById(1)).thenReturn(Optional.of(mockStagiaire));
        when(templateDao.findByType(TypeNotification.WARNING_ITEM)).thenReturn(mockTemplate);

        notificationService.notifyStagiaireOnDocumentNotValidated(1);

        verify(notificationDao, times(1)).save(any(Notification.class));
    }

    @Test
    void testNotifyStagiaireOnAbsenceThreshold_WithUser() {
        when(userDao.findById(1)).thenReturn(Optional.of(mockStagiaire));
        when(templateDao.findByType(TypeNotification.WARNING_ABSENCE)).thenReturn(mockTemplate);

        notificationService.notifyStagiaireOnAbsenceThreshold(1);

        verify(notificationDao, times(1)).save(any(Notification.class));
    }

    @Test
    void testRemindStagiaireToCompleteFile_WithUser() {
        when(userDao.findById(1)).thenReturn(Optional.of(mockStagiaire));
        when(templateDao.findByType(TypeNotification.RAPPEL)).thenReturn(mockTemplate);

        notificationService.remindStagiaireToCompleteFile(1);

        verify(notificationDao, times(1)).save(any(Notification.class));
    }

    @Test
    void testMarkNotificationAsRead_WithExistingNotification() {
        Notification notification = mock(Notification.class);
        when(notificationDao.findById(1)).thenReturn(Optional.of(notification));

        notificationService.markNotificationAsRead(1);

        verify(notification).setRead(true);
        verify(notificationDao).save(notification);
    }

    @Test
    void testMarkNotificationAsRead_NoNotification() {
        when(notificationDao.findById(1)).thenReturn(Optional.empty());

        notificationService.markNotificationAsRead(1);

        verify(notificationDao, never()).save(any());
    }

    @Test
    void testGetNotificationsForUser() {
        List<Notification> notifications = Arrays.asList(mock(Notification.class));
        when(notificationDao.findByDestinataireId(1)).thenReturn(notifications);

        List<Notification> result = notificationService.getNotificationsForUser(1);

        assertEquals(notifications, result);
    }

    @Test
    void testNotifyStagiaireOnDelayThreshold_WithUser() {
        when(userDao.findById(1)).thenReturn(Optional.of(mockStagiaire));
        when(templateDao.findByType(TypeNotification.WARNING_DELAY)).thenReturn(mockTemplate);

        notificationService.notifyStagiaireOnDelayThreshold(1);

        verify(notificationDao, times(1)).save(any(Notification.class));
    }

    /**
     * Teste la création d’une notification personnalisée envoyée à un administrateur.
     */
    @Test
    void testSendCustomAdminNotification() {
        when(templateDao.findByType(TypeNotification.INFORMATION)).thenReturn(mockTemplate);

        notificationService.sendCustomAdminNotification(mockAdmin, 1, "Absence", "Seuil atteint");

        verify(notificationDao, times(1)).save(any(Notification.class));
    }

    /**
     * Teste l’envoi d’une notification de validation de document
     * lorsque le stagiaire existe et que le template est disponible.
     */
    @Test
    void testNotifyStagiaireOnDocumentValidated_success() {
        int stagiaireId = 1;
        when(userDao.findById(stagiaireId)).thenReturn(Optional.of(mockStagiaire));
        when(templateDao.findByType(TypeNotification.CONFIRMATION)).thenReturn(mockTemplate);

        notificationService.notifyStagiaireOnDocumentValidated(stagiaireId);

        verify(notificationDao, times(1)).save(any(Notification.class));
    }

    /**
     * Teste le comportement lorsque le stagiaire destinataire n’existe pas.
     * La notification ne doit pas être sauvegardée.
     */
    @Test
    void testNotifyStagiaireOnDocumentValidated_noUser() {
        int stagiaireId = 2;
        when(userDao.findById(stagiaireId)).thenReturn(Optional.empty());

        notificationService.notifyStagiaireOnDocumentValidated(stagiaireId);

        verify(notificationDao, never()).save(any());
    }

    /**
     * Teste l’envoi d’une notification lorsque le document est rejeté.
     */
    @Test
    void testNotifyStagiaireOnDocumentNotValidated_success() {
        int stagiaireId = 3;
        when(userDao.findById(stagiaireId)).thenReturn(Optional.of(mockStagiaire));
        when(templateDao.findByType(TypeNotification.WARNING_ITEM)).thenReturn(mockTemplate);

        notificationService.notifyStagiaireOnDocumentNotValidated(stagiaireId);

        verify(notificationDao, times(1)).save(any(Notification.class));
    }


    // Tu peux ajouter d'autres tests similaires pour notifyStagiaireOnAbsenceThreshold,
    // notifyStagiaireOnDelayThreshold, remindStagiaireToCompleteFile, etc.
}
