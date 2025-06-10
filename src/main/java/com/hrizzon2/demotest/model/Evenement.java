package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "evenement")
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evenement", unique = true, nullable = false)
    private Integer id;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;


    /**
     * Par défaut false, indique si l’événement est signalé en retard.
     */
    @Column(name = "est_retard", nullable = false)
    private boolean estRetard = false;

    /**
     * Chaque événement est rattaché à un stagiaire (non nullable).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stagiaire_id", nullable = false)
    private Stagiaire stagiaire;

    /**
     * Chaque événement a un motif (non nullable).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "motif_id", nullable = false)
    private Motif motif;

    /**
     * Facultatif : si l’événement est justifié par un document, on met sa FK ici.
     * Si pas de justification, laisser NULL.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = true)
    private Document document;

}
