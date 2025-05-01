package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("STAGIAIRE")
public class Stagiaire extends User {


    @Column(unique = true, nullable = false)
    private Date dateNaissance;

    @Column(unique = true, nullable = false)
    private Number numeroDeTelephone;

    @Column(unique = true, nullable = false)
    private String adresse;

    @OneToMany(mappedBy = "stagiaire")
    private List<Evenement> evenements;
    // TODO créer classe Evenement

    @OneToMany(mappedBy = "stagiaire")
    private List<Dossier> dossiers;

    @ManyToOne
    private Inscription inscription;


}
