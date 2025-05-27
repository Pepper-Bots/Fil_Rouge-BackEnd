package com.hrizzon2.demotest.model;

import com.hrizzon2.demotest.model.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// ingrédient apporté

@Getter
@Setter
@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TypeDocument type;

    @ManyToOne
    @JoinColumn(name = "statut_id", nullable = false)
    private StatutDocument statut;


    @ManyToOne
    @JoinColumn(name = "dossier_id")
    private Dossier dossier;

    // TODO Optionnel mais conseillé :
    //Ajoute une colonne pour la date d’upload ou le fichier (blob/url), à voir plus tard.
}
