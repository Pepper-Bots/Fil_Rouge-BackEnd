package com.hrizzon2.demotest.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "region")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_region")
    private String idRegion;

    private String nomRegion;

    private String nomPays;

    @OneToMany(mappedBy = "region")
    private List<Ville> villes;
}
