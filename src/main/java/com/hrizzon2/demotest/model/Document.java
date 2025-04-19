package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TypeDocument type;

    @Enumerated(EnumType.STRING)
    private StatutDocument statut;

    @ManyToOne
    @JoinColumn(name = "dossier_id")
    private Dossier dossier;
}
