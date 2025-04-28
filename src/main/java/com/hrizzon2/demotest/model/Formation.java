package com.hrizzon2.demotest.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.view.AffichageDossier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(AffichageDossier.Formation.class)
    private Integer id;

    @JsonView(AffichageDossier.Formation.class)
    protected String titre;

    @OneToMany(mappedBy = "formation")
    private List<Inscription> inscriptions;

}
