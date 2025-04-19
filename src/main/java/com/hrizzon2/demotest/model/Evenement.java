package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private boolean estRetard = false;
    // ou ENUM pour RETARD / ABSENCE

    @ManyToOne
    private Stagiaire stagiaire;

    @ManyToOne
    private Motif motif;


}
