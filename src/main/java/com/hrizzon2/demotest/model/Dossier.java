package com.hrizzon2.demotest.model;
// Entités JPA

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.view.AffichageDossier;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

// plat cuisiné (dossier d'inscription final)

/**
 * Entité représentant un dossier d'inscription lié à un stagiaire et une formation.
 */
@Getter
@Setter
@Entity
@Table(name = "dossier")
public class Dossier {

    /**
     * Identifiant unique du dossier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(AffichageDossier.Dossier.class)
    @Column(name = "id_dossier")
    protected Integer id;


    /**
     * Code métier unique du dossier (longueur 2 à 10).
     */
    @Column(name = "code_dossier", length = 10, nullable = false, unique = true)
    @Length(min = 2, max = 10)
    @NotBlank
    @JsonView(AffichageDossier.Dossier.class)
    protected String codeDossier;

    /**
     * Statut global du dossier (FK vers StatutDossier).
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statut_dossier_id", nullable = false)
    @JsonView(AffichageDossier.Dossier.class)
    protected StatutDossier statutDossier;

    /**
     * Liste des documents associés à ce dossier.
     * Si vous souhaitez contrôler l’arborescence Doc↔StatutDocument,
     * c’est à Document de référencer StatutDocument, pas l’inverse.
     */
    @OneToMany(mappedBy = "dossier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents;

    /**
     * Date de création du dossier (non nullable).
     */
    @Column(name = "date_de_creation", nullable = false)
    @NotNull
    @JsonView(AffichageDossier.Dossier.class)
    protected LocalDateTime dateCreation;

    /**
     * Date/heure de la dernière mise à jour (mise à jour automatique).
     */
    @Column(name = "derniere_mise_a_jour")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime derniereMiseAJour;

    /**
     * Date/heure de la dernière modification (mise à jour automatique).
     */
    @Column(name = "date_modification")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateModification;

    /**
     * Callback JPA pour initialiser et mettre à jour les timestamp :
     * - @PrePersist : dateCreation + dateModification + derniereMiseAJour = maintenant
     * - @PreUpdate  : dateModification + derniereMiseAJour = maintenant
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.dateCreation = now;
        this.dateModification = now;
        this.derniereMiseAJour = now;
    }

    @PreUpdate
    public void preUpdate() {
        LocalDateTime now = LocalDateTime.now();
        this.dateModification = now;
        this.derniereMiseAJour = now;
    }

    /**
     * Le stagiaire auquel ce dossier appartient (FK non-null).
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stagiaire_id", nullable = false)
    private Stagiaire stagiaire;


    /**
     * Lien vers la formation concernée par ce dossier (FK non nullable)
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    /**
     * L’admin qui a créé ce dossier (FK non nullable)
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id", nullable = false)
    private Admin createur;


    // === GETTERS DÉRIVÉS POUR AFFICHAGE SIMPLIFIÉ ===

    // === Methods utilitaires pour l’affichage (JsonView) ===

    /**
     * Renvoie “Prénom Nom” du stagiaire pour l’affichage simplifié.
     */
    @JsonView(AffichageDossier.Stagiaire.class)
    public String getNomPrenomStagiaire() {
        return (stagiaire != null)
                ? stagiaire.getFirstName() + " " + stagiaire.getLastName()
                : null;
    }

    /**
     * Renvoie le “titre” de la formation (ici on prend simplement le champ nom).
     */
    @JsonView(AffichageDossier.Formation.class)
    public String getTitreFormation() {
        return (formation != null) ? formation.getNom() : null;
    }

    /**
     * Renvoie le nom de famille de l’admin qui a créé le dossier.
     */
    @JsonView(AffichageDossier.Admin.class)
    public String getNomCreateur() {
        return (createur != null) ? createur.getLastName() : null;
    }

    /**
     * Un champ pour stocker le nom du fichier image (au besoin).
     */
    @JsonView({AffichageDossier.Stagiaire.class})
    private String nomImage;
}





