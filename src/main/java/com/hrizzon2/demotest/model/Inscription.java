package com.hrizzon2.demotest.model;

import com.hrizzon2.demotest.model.enums.StatutInscription;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@SuppressWarnings("unused")
public class Inscription {

    // Cf Etat de CDA_Demo_Test

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate dateCreation;
    private LocalDate dateModification;
    private LocalDate dateValidation;

    @Enumerated(EnumType.STRING)
    private StatutInscription statut;

    @ManyToOne
    private Stagiaire stagiaire;

    @ManyToOne
    @JoinColumn(name = "formation_id")
    private Formation formation;

    @OneToOne(cascade = CascadeType.ALL)
    private Dossier dossier;

}
