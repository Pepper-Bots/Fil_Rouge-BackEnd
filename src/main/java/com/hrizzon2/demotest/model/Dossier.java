package com.hrizzon2.demotest.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.view.AffichageDossier;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.Instant;

@Getter
@Setter
@Entity
public class Dossier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    // TODO - Insérer Clé étrangère IdStagiaire
    // TODO - Insérer Clé étrangère IdFormation
    

    @Column(length = 20, nullable = false, unique = true)
    @Length(max = 20, min = 3)
    @NotBlank
    @JsonView(AffichageDossier.class)
    protected String nomStagiaire;

    @Column(length = 20, nullable = false, unique = true)
    @Length(max = 20, min = 3)
    @NotBlank
    @JsonView(AffichageDossier.class)
    protected String prenomStagiaire;

    @Column(nullable = false)
    @JsonView(AffichageDossier.class)
    protected String nomFormation;

    @ManyToOne
    @JoinColumn(nullable = false)
    protected EtatDossier etatDossier;

    @Column(nullable = false)
    Instant dateDeCreation;

    @Column(nullable = false)
    Instant dateDeModification;

//    @Enumerated(EnumType.STRING)
//    @Column(columnDefinition =
//            "ENUM('COMPLET', 'INCOMPLET', 'VALIDE', 'EN COURS DE VALIDATION')")
//    private Status status;

    // Insérer statut -> COMPLET / INCOMPLET / VALIDE / EN ATTENTE DE VALIDATION => Enum ?

}
