package com.hrizzon2.demotest.model;

import com.hrizzon2.demotest.user.model.Ville;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "region")
public class Region {

    @Id
    private String idRegion;

    private String nomRegion;

    private String nomPays;

    @OneToMany(mappedBy = "region")
    private List<Ville> villes;
}
