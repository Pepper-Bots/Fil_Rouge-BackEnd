package com.hrizzon2.demotest.document.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "statut_document")
public class StatutDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_document")
    protected Integer id;

    @Column(nullable = false)
    protected String nom;

}
