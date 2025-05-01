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
     * Nom du dossier (ex: nom du stagiaire).
     */
    @Column(nullable = false)
    @JsonView(AffichageDossier.Dossier.class)
    public String getNomStagiaire() {
        return (stagiaire != null) ? stagiaire.getNom() : null;
    }

    /**
     * Code unique d'identification du dossier.
     */
    @Column(length = 10, nullable = false, unique = true)
    @Length(max = 10, min = 2)
    @NotBlank
    @JsonView(AffichageDossier.class)
    protected String codeDossier;

    /**
     * Statut global du dossier.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonView(AffichageDossier.Dossier.class)
    protected StatutDossier statutDossier;
    // TODO -> le statut du dossier dépend du statut du document => si tous les documents (Liste complète) sont fournis + validés -> dossier validé

    // TODO => Liste Document + statutDocument ??
    /**
     * Liste des documents associés au dossier.
     */
    @OneToMany(mappedBy = "dossier", cascade = CascadeType.ALL, orphanRemoval = true) // TODO -> orphanRemoval = true ?
    private List<Document> documents;
    // TODO -> associer à TypeDocument (ENUM)

    // Gestion du status du document
    // TODO -> comment le faire passer d'un statut à un autre ?
    /**
     * Statut des documents dans le dossier.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statut_document_id", nullable = false)
    private StatutDocument statutDocument;


    /**
     * Date et heure de la dernière mise à jour du dossier.
     */
    @Column(name = "last_updated")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;

    /**
     * Met à jour automatiquement la date de dernière modification.
     */
    @PrePersist
    @PreUpdate
    public void updateTimeStamp() {
        this.lastUpdated = LocalDateTime.now();
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
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;


    // TODO créer une table de jointure pour formation + document ?
//    @ManyToMany
//    // table de jointure
//    @JoinTable(
//            name = "etiquette_product",
//            inverseJoinColumns = @JoinColumn(name = "etiquette_id")
//    )
//    protected List<Etiquette> etiquettes = new ArrayList<>();

    /**
     * Administrateur créateur du dossier.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id", nullable = false)
    private Admin createur;
}

