package com.hrizzon2.demotest.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.view.AffichageDossier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@SuppressWarnings("unused")
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(AffichageDossier.Formation.class)
    private Integer id;

    @JsonView(AffichageDossier.Formation.class)
    protected String titre;

    @JsonView(AffichageDossier.Formation.class)
    protected String description;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @OneToMany(mappedBy = "formation")
    private List<Inscription> inscriptions;

    @OneToMany(mappedBy = "formation")
    private List<Dossier> dossiers;
}
