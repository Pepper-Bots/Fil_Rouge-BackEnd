package com.hrizzon2.demotest.dto;

import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Ce DTO n’est pas directement le statut d’un document,
// mais plutôt la vue d’un document transmis pour l’UI (statut, obligatoire, fichier, etc.)

@Getter
@Setter
public class DocumentSummaryDto {

    private TypeDocument typeDocument;      // Ex : CV
    private boolean obligatoire;            // Toujours true ici
    private boolean transmis;               // Est-ce qu’il existe un PieceJointeStagiaire ?
    private String statut;                  // Validé / En attente / Refusé / null si pas transmis
    private String commentaire;             // Ajouté : commentaire du document
    private String fichier;                 // URL ou nom du fichier transmis (si existant)
    private LocalDateTime dateDepot;        // Date de dépôt du document

}
