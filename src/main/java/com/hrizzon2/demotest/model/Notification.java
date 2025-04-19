package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Notification {

    @Id
    @GeneratedValue
    private long id;

    @Enumerated(EnumType.STRING)
    private TypeNotification type;

    private String message;

    @ManyToOne
    private User destinataire;
}
