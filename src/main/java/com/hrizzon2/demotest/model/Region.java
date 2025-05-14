package com.hrizzon2.demotest.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "region")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRegion;

    private String nomRegion;

    private String nomPays;

    @OneToMany(mappedBy = "region")
    private List<Ville> villes;
}
