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
public class Dossier {

    /**
     * Identifiant unique du dossier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(AffichageDossier.Dossier.class)
    protected Integer id;

    /**
     * Code unique d'identification du dossier.
     */
    @Column(length = 10, nullable = false, unique = true)
    @Length(max = 10, min = 2)
    @NotBlank
    @JsonView(AffichageDossier.Dossier.class)
    protected String codeDossier;
    // TODO Optionnel si tu utilises l’id, mais tu peux le garder pour une logique métier ou un affichage utilisateur.

    // validation du plat

    /**
     * Statut global du dossier.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statut_dossier_id", nullable = false)
    @JsonView(AffichageDossier.Dossier.class)
    protected StatutDossier statutDossier;

    // Gestion du status du document - TODO pas nécessaire
    /**
     * Statut des documents dans le dossier.
     */
//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "statut_document_id", nullable = false)
//    private StatutDocument statutDocument;

    // Si on souhaite stocker une arborescence (Dossier 1↔∗Document∈Status),
    // mieux vaut que Document référence StatutDocument et qu’un calcul métier détermine
    // “est-ce que tous les documents du dossier sont validés ?”

    /**
     * Liste des documents associés au dossier.
     */
    @OneToMany(mappedBy = "dossier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents;


    @Column(name = "date_de_creation", nullable = false)
    @NotNull
    @JsonView(AffichageDossier.Dossier.class)
    protected LocalDateTime dateCreation;

    /**
     * Date et heure de la dernière mise à jour du dossier.
     */
    @Column(name = "last_updated")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;

    /**
     * Date et heure de modification du dossier.
     */
    @Column(name = "date_modification")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateModification;

    /**
     * Met à jour automatiquement la date de dernière modification.
     */
    @PrePersist
    @PreUpdate
    public void updateTimeStamp() {
        this.lastUpdated = LocalDateTime.now();
        this.dateModification = LocalDateTime.now(); // Synchroniser les deux champs
    }

    /**
     * Lien vers le stagiaire concerné.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stagiaire_id", nullable = false)
    private Stagiaire stagiaire;
    // Un dossier ne peut pas être créé sans stagiaire

    /**
     * Lien vers la formation concernée.
     */
    @Getter
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    /**
     * Administrateur créateur du dossier.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id", nullable = false)
    private Admin createur;


    // === GETTERS DÉRIVÉS POUR AFFICHAGE SIMPLIFIÉ ===

    /**
     * Nom et prénom du dossier (ex: nom du stagiaire).
     */
    @JsonView(AffichageDossier.Stagiaire.class)
    public String getNomPrenomStagiaire() {
        return (stagiaire != null)
                ? stagiaire.getFirstName() + " " + stagiaire.getLastName()
                : null;
    }

    // TODO -> méthodes à utiliser ?

    @JsonView(AffichageDossier.Formation.class)
    public String getTitreFormation() {
        return (formation != null) ? formation.getTitre() : null;
    }

    @JsonView(AffichageDossier.Admin.class)
    public String getNomCreateur() {
        return (createur != null) ? createur.getLastName() : null;
    }

    @JsonView({AffichageDossier.Stagiaire.class})
    String nomImage;

}

// TODO -> orphanRemoval = true ?
// TODO -> le statut du dossier dépend du statut du document => si tous les documents (Liste complète) sont fournis + validés -> dossier validé

// TODO => Liste Document + statutDocument ??
// TODO créer une table de jointure pour formation + document ?
// TODO -> associer à TypeDocument (ENUM)
// TODO -> comment le faire passer d'un statut à un autre ?
