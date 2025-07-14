package com.hrizzon2.demotest.inscription.model;

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
    protected String nomStatut;

}

//    EN_ATTENTE_DE_VALIDATION,
//    COMPLET,
//    INCOMPLET,
//    VALIDE
//
