package com.hrizzon2.demotest.model;

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
