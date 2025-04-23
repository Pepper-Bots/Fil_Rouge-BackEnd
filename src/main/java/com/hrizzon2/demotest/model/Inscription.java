package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Inscription {

    // Cf Etat de CDA_Demo_Test

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private StatutInscription statut;

    @ManyToOne
    private Stagiaire stagiaire;

    @ManyToOne
    private Formation formation;

}
