package com.hrizzon2.demotest.model;

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

@Getter
@Setter
@Entity
public class Dossier {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(nullable = false)
    @JsonView(AffichageDossier.class)
    protected String nom; // TODO nom du stagiaire ?

    @Column(length = 10, nullable = false, unique = true)
    @Length(max = 10, min = 2)
    @NotBlank
    @JsonView(AffichageDossier.class)
    protected String codeDossier;


    // ENUM : COMPLET / INCOMPLET / VALIDE / EN ATTENTE
//    @Enumerated(EnumType.STRING)
//    private StatutDossier statut = StatutDossier.EN_ATTENTE_DE_VALIDATION;


    // Gestion du status du dossier
    // TODO -> comment le faire passer d'un statut à un autre ?
    @ManyToOne
    @JoinColumn(nullable = false)
    protected StatutDossier statutDossier;
    // TODO -> le statut du dossier dépend du statut du document => si tous les documents (Liste complète) sont fournis + validés -> dossier validé

    // TODO => Liste Document + statutDocument ??
    @OneToMany(mappedBy = "dossier", cascade = CascadeType.ALL)
    // TODO -> à vérifier
    private List<Document> documents; // TODO -> associer à TypeDocument (ENUM)

    // Gestion du status du document
    // TODO -> comment le faire passer d'un statut à un autre ?
    @ManyToOne
    @JoinColumn(nullable = false)
    protected StatutDocument statutDocument;


    //    // Date de dernière mise à jour automatique (optionnel)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;

    // Lien vers le stagiaire concerné
    // Un dossier ne peut pas être créé sans stagiaire
    @NotNull
    @ManyToOne
    @JoinColumn(name = "stagiaire_id", nullable = false)
    private Stagiaire stagiaire;

    // Association table formation
    @NotNull
    @ManyToOne
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


    @PreUpdate
    public void updateTimeStamp() {
        this.lastUpdated = LocalDateTime.now();
        // Dernière mise à jour du dossier
        // TODO : @Annotations ??
    }

    @ManyToOne
    @JoinColumn(nullable = false)
    Admin createur;
}

