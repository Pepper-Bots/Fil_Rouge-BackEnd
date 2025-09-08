package com.hrizzon2.demotest.notification.model;

import com.hrizzon2.demotest.notification.model.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "notification_template")
public class NotificationTemplate {

    @Id
    @GeneratedValue
    @Column(name = "id_notification_template")
    private Integer id;

    @Enumerated(EnumType.STRING)
    private TypeNotification type;

    private String message;
}
