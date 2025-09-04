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
    @Column(name = "id_document")
    private Integer id;

    // ========== Relations principales =============//

    /**
     * Association vers le stagiaire qui a soumis ce document
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_Stagiaire", nullable = false)
    private Stagiaire stagiaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_document", nullable = false)
    private StatutDocument statut;

    // ========== Relations contextuelles (une seule non-null à la fois) =============//

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dossier", nullable = true)
    private Dossier dossier;  // Pour documents d'inscription

    /**
     * Association optionnelle vers l'évènement justifié par ce document
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Id_evenement", nullable = true)
    private Evenement evenement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formation", nullable = true)
    public Formation formation;


    // ========== Attributs =============//

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Enumerated(EnumType.STRING)
    private TypeDocument typeDocument;

    /**
     * Date et heure du dépôt du document
     */
    @Column(name = "date_depot", nullable = false)
    private LocalDateTime dateDepot;

    /**
     * URL ou chemin vers le fichier, selon votre implémentation
     */
    @Column(name = "url_fichier")
    private String urlFichier;

    /**
     * Pour stocker un éventuel commentaire en cas de refus
     */
    @Column(length = 1000)
    private String commentaire;
}
