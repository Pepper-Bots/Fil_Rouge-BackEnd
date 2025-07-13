package com.hrizzon2.demotest.notification;

import com.hrizzon2.demotest.model.NotificationTemplate;
import com.hrizzon2.demotest.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@SuppressWarnings("unused")
public class Notification {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private NotificationTemplate template;

    @ManyToOne
    private User destinataire;

    private boolean isRead = false;

    private LocalDateTime date; // Date d'envoi de la modification

}
