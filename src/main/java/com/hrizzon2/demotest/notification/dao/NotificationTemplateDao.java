package com.hrizzon2.demotest.notification.dao;

import com.hrizzon2.demotest.notification.model.NotificationTemplate;
import com.hrizzon2.demotest.notification.model.enums.TypeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTemplateDao extends JpaRepository<NotificationTemplate, Integer> {

    NotificationTemplate findByType(TypeNotification type);
}
