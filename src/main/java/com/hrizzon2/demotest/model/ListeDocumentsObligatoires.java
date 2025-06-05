package com.hrizzon2.demotest.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import com.hrizzon2.demotest.view.AffichageListeDocumentsObligatoires;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Entité qui relie Formation à Type de document pour créer une liste attendue de docs

// Ici liste d'ingrédients

@Getter
@Setter
@Entity
public class ListeDocumentsObligatoires {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(AffichageListeDocumentsObligatoires.Resume.class)
    private Integer id;

    @JsonView(AffichageListeDocumentsObligatoires.Complet.class)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Formation formation;

    @Enumerated(EnumType.STRING)
    @JsonView(AffichageListeDocumentsObligatoires.Resume.class)
    private TypeDocument typeDocument;
}
