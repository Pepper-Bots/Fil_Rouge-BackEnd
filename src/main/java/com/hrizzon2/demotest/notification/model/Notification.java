package com.hrizzon2.demotest.notification.model;

import com.hrizzon2.demotest.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue
    @Column(name = "id_notification")
    private Integer id;

    @ManyToOne
    private NotificationTemplate template;

    @ManyToOne
    private User destinataire;

    private boolean isRead = false;

    private LocalDateTime date; // Date d'envoi de la modification

}
