package com.hrizzon2.demotest.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "ville")
public class Ville {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idVille;

    private String codePostal;

    private String nomVille;

    private String villeStagiaire;

    @ManyToOne
    @JoinColumn(name = "id_region")
    private Region region;

    @OneToMany(mappedBy = "ville")
    private List<Stagiaire> stagiaires;
}
