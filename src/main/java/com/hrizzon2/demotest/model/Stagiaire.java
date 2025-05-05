package com.hrizzon2.demotest.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Représente un stagiaire, qui hérite de la classe User.
 * Contient des informations personnelles spécifiques ainsi que les relations
 * avec les événements, dossiers et inscriptions.
 */
@Getter
@Setter
@Entity
@DiscriminatorValue("STAGIAIRE")
public class Stagiaire extends User {

    /**
     * Date de naissance du stagiaire.
     */
    @Column(nullable = false)
    private Date dateNaissance;

    /**
     * Numéro de téléphone du stagiaire.
     */
    @Column(nullable = false)
    private Number numeroDeTelephone;

    /**
     * Adresse du stagiaire.
     */
    @Column(nullable = false)
    private String adresse;

    /**
     * Liste des événements liés à ce stagiaire.
     */
    @OneToMany(mappedBy = "stagiaire")
    private List<Evenement> evenements;

    /**
     * Liste des dossiers liés à ce stagiaire.
     */
    @OneToMany(mappedBy = "stagiaire")
    private List<Dossier> dossiers;

    /**
     * Liste des inscriptions effectuées par ce stagiaire.
     */
    @OneToMany(mappedBy = "stagiaire")
    private List<Inscription> inscriptions;


}
