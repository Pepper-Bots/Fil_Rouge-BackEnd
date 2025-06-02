package com.hrizzon2.demotest.model;

import com.hrizzon2.demotest.model.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entité représentant un document transmis par un stagiaire pour une formation donnée.
 */
@Getter
@Setter
@Entity
public class PieceJointeStagiaire {

    @Id
    @GeneratedValue
    private Integer id;

    /**
     * Stagiaire ayant transmis le document.
     */
    @ManyToOne(optional = false)
    private Stagiaire stagiaire;

    /**
     * Formation à laquelle le document est rattaché.
     */
    @ManyToOne(optional = false)
    private Formation formation;

    /**
     * Type du document transmis (CV, Carte d'identité...).
     */
    @Enumerated(EnumType.STRING)
    private TypeDocument typeDocument;

    /**
     * Chemin ou URL du fichier transmis.
     */
    private String fichier; // chemin/URL du fichier transmis

    /**
     * Statut actuel du document (EN_ATTENTE, VALIDE, REFUSE...).
     */
    @ManyToOne
    private StatutDocument statutDocument;

    private String commentaire;


    // TODO Ajoute une date d'upload si besoin

}
