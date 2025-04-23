package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StatutDossier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(nullable = false)
    protected String nom;

}

//    EN_ATTENTE_DE_VALIDATION,
//    COMPLET,
//    INCOMPLET,
//    VALIDE
//
