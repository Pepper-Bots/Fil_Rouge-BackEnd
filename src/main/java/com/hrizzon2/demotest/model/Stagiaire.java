package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Représente un stagiaire dans le système.
 *
 * <p>Cette classe hérite de {@link User} et contient des informations
 * personnelles supplémentaires spécifiques aux stagiaires, telles que
 * la date de naissance, le numéro de téléphone et l'adresse.</p>
 *
 * <p>Elle établit également des relations avec d'autres entités comme
 * {@link Evenement}, {@link Dossier} et {@link Inscription}.</p>
 *
 * <p>La valeur de discrimination dans la base de données est {@code STAGIAIRE},
 * utilisée dans la stratégie d’héritage JOINED.</p>
 *
 * @see User
 * @see Evenement
 * @see Dossier
 * @see Inscription
 */
@Getter
@Setter
@Entity
@DiscriminatorValue("STAGIAIRE")
public class Stagiaire extends User {


    @Column(nullable = false)
    private boolean premiereConnexion = true;

    /**
     * Date de naissance du stagiaire.
     * Ce champ est obligatoire.
     */
    @Temporal(jakarta.persistence.TemporalType.DATE)
    @Column(nullable = false)
    private Date dateNaissance;

    /**
     * Numéro de téléphone du stagiaire.
     * Ce champ est obligatoire.
     */
    @Column(nullable = false)
    private String phoneNumber;

    /**
     * Adresse postale du stagiaire.
     * Ce champ est obligatoire.
     */
    @Column(nullable = false)
    private String adresse;

    @ManyToOne
    @JoinColumn(name = "ville_id", nullable = false)
    private Ville ville;

    /**
     * Liste des événements associés à ce stagiaire.
     */
    @OneToMany(mappedBy = "stagiaire")
    private List<Evenement> evenements;

    /**
     * Liste des dossiers associés à ce stagiaire.
     */
    @OneToMany(mappedBy = "stagiaire")
    private List<Dossier> dossiers;

    /**
     * Liste des inscriptions effectuées par ce stagiaire.
     */
    @OneToMany(mappedBy = "stagiaire")
    private List<Inscription> inscriptions;

    @Column
    private String activationToken;


}
