package com.hrizzon2.demotest.formation.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.formation.model.enums.NiveauFormation;
import com.hrizzon2.demotest.inscription.model.Dossier;
import com.hrizzon2.demotest.inscription.model.Inscription;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.view.AffichageDossier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "formation")
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(AffichageDossier.Formation.class)
    @Column(name = "id_formation")
    private Integer id;

    @JsonView(AffichageDossier.Formation.class)
    @Column(name = "nom", length = 80, nullable = false)
    protected String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau", length = 20)
    private NiveauFormation niveau;

    @JsonView(AffichageDossier.Formation.class)
    @Column(name = "description", length = 255)
    protected String description;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @OneToMany(mappedBy = "formation", fetch = FetchType.LAZY)
    private List<Inscription> inscriptions;

    @OneToMany(mappedBy = "formation", fetch = FetchType.LAZY)
    private List<Dossier> dossiers;

    /**
     * Méthode utilitaire pour renvoyer le nom comme titre.
     */
    @Transient
    public String getTitre() {
        return this.nom;
    }

    @OneToMany(mappedBy = "formation", fetch = FetchType.LAZY)
    private List<ListeDocumentsObligatoires> listeDocumentsObligatoires;

    @Transient
    public List<TypeDocument> getListeDocumentsObligatoires() {
        if (listeDocumentsObligatoires == null) return List.of();

        return listeDocumentsObligatoires.stream()
                .map(ListeDocumentsObligatoires::getTypeDocument) // méthode à adapter
                .toList();
    }

}
