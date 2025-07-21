package com.hrizzon2.demotest.document.model;

import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.evenement.model.Evenement;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.inscription.model.Dossier;
import com.hrizzon2.demotest.user.model.Stagiaire;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_id")
    private Dossier dossier;

    /**
     * Association vers le stagiaire qui a soumis ce document
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stagiaire_id", nullable = false)
    private Stagiaire stagiaire;

    /**
     * Association optionnelle vers l'évènement justifié par ce document
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id", nullable = true)
    private Evenement evenement;

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

    @ManyToOne
    @JoinColumn(name = "formation_id_formation")
    public Formation formation;
}
