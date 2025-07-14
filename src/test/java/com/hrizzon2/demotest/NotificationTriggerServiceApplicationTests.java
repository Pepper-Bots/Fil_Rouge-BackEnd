package com.hrizzon2.demotest;

import com.hrizzon2.demotest.model.enums.NiveauDroit;
import com.hrizzon2.demotest.notification.NotificationService;
import com.hrizzon2.demotest.notification.NotificationTriggerService;
import com.hrizzon2.demotest.user.service.AdminService;
import com.hrizzon2.demotest.user.model.Admin;
import com.hrizzon2.demotest.user.model.enums.TypeAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class NotificationTriggerServiceApplicationTests {

    @Mock
    private NotificationService notificationService;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private NotificationTriggerService triggerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotifyAdminsSeuilAtteint_multipleTypes() {
        int stagiaireId = 123;
        String type = "Absence";

        Admin admin1 = mock(Admin.class);
        Admin admin2 = mock(Admin.class);

        when(adminService.getAdminsByTypeAndNiveau(TypeAdmin.ASSISTANT_VIE_SCOLAIRE, NiveauDroit.ADMIN))
                .thenReturn(Arrays.asList(admin1));

        when(adminService.getAdminsByTypeAndNiveau(TypeAdmin.RESPONSABLE_FORMATION, NiveauDroit.ADMIN))
                .thenReturn(Arrays.asList(admin2));

        List<TypeAdmin> typesAdmin = Arrays.asList(TypeAdmin.ASSISTANT_VIE_SCOLAIRE, TypeAdmin.RESPONSABLE_FORMATION);

        triggerService.notifyAdminsSeuilAtteint(stagiaireId, type, typesAdmin);

        verify(notificationService, times(1))
                .sendCustomAdminNotification(admin1, stagiaireId, type, "Seuil atteint");
        verify(notificationService, times(1))
                .sendCustomAdminNotification(admin2, stagiaireId, type, "Seuil atteint");
    }
}
