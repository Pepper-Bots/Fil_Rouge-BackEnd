package com.hrizzon2.demotest.model;

import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import com.hrizzon2.demotest.user.model.Stagiaire;
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
    @JoinColumn(name = "stagiaire_id", nullable = false)
    private Stagiaire stagiaire;

    /**
     * Formation à laquelle le document est rattaché.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    /**
     * Type du document transmis (CV, Carte d'identité...).
     */
    @Enumerated(EnumType.STRING)
    private TypeDocument typeDocument;

    /**
     * Chemin ou URL du fichier transmis.
     */
    private String cheminFichier; // chemin/URL du fichier transmis

    /**
     * Statut actuel du document (EN_ATTENTE, VALIDE, REFUSE...).
     */
    @ManyToOne
    @JoinColumn(name = "statut_document_id", nullable = false)
    private StatutDocument statutDocument;

    private String commentaire;

}