package com.hrizzon2.demotest.mock;

import com.hrizzon2.demotest.notification.dao.NotificationDao;
import com.hrizzon2.demotest.notification.dao.NotificationTemplateDao;
import com.hrizzon2.demotest.user.dao.UserDao;
import com.hrizzon2.demotest.user.service.AdminService;
import com.hrizzon2.demotest.user.service.Stagiaire.StagiaireService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Spring pour fournir des mocks de DAO et services
 * utilisés dans les tests unitaires.
 */
@Configuration
public class MockConfig {

    @Bean
    public NotificationDao notificationDao() {
        return Mockito.mock(NotificationDao.class);
    }

    @Bean
    public NotificationTemplateDao notificationTemplateDao() {
        return Mockito.mock(NotificationTemplateDao.class);
    }

    @Bean
    public UserDao userDao() {
        return Mockito.mock(UserDao.class);
    }

    @Bean
    public AdminService adminService() {
        return Mockito.mock(AdminService.class);
    }

    @Bean
    public StagiaireService stagiaireService() {
        return Mockito.mock(StagiaireService.class);
    }

    // Ajoute ici d'autres mocks nécessaires (ex: MockSecurityUtils, MockStagiaireDao etc.)
}
