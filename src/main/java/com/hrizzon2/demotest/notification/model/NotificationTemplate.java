package com.hrizzon2.demotest.notification.model;

import com.hrizzon2.demotest.notification.model.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@SuppressWarnings("unused")
public class NotificationTemplate {

    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    private TypeNotification type;

    private String message;
}
