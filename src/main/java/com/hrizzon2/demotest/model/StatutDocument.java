package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StatutDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(nullable = false)
    protected String nom;

}

// => Table StatutDocument à la place d'un ENUM
//  ENVOYE, VALIDE, REFUSE, MANQUANT