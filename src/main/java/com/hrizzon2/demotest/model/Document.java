package com.hrizzon2.demotest.model;

import com.hrizzon2.demotest.model.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Enumerated(EnumType.STRING) // TODO ça n'est plus un enum
    private StatutDocument statut;

    @ManyToOne
    @JoinColumn(name = "dossier_id")
    private Dossier dossier;
}
