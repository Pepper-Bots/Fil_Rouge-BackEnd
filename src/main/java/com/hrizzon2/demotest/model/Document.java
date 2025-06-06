package com.hrizzon2.demotest.model;

import com.hrizzon2.demotest.model.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// ingrédient apporté

@Getter
@Setter
@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nomFichier;

    @Enumerated(EnumType.STRING)
    private TypeDocument type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statut_document_id", nullable = false)
    private StatutDocument statut;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "dossier_id")
//    private Dossier dossier;

    /**
     * Pour stocker un éventuel commentaire en cas de refus
     */
    @Column(length = 500)
    private String commentaire;

    /**
     * Date et heure du dépôt du document
     */
    private LocalDateTime dateDepot;

    /**
     * URL ou chemin vers le fichier, selon votre implémentation
     */
    private String urlFichier;


}
