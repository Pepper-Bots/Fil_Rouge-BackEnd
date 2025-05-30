package com.hrizzon2.demotest.model;

import com.hrizzon2.demotest.model.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DocumentStagiaire {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Stagiaire stagiaire;

    @ManyToOne
    private Formation formation;

    @Enumerated(EnumType.STRING)
    private TypeDocument typeDocument;

    private String fichier; // chemin/URL du fichier transmis

    @ManyToOne
    private StatutDocument statutDocument;

}
