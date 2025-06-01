package com.hrizzon2.demotest.dto;

import com.hrizzon2.demotest.model.enums.TypeDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentStatutDTO {
    private TypeDocument typeDocument;      // Ex : CV
    private boolean obligatoire;            // Toujours true ici
    private boolean transmis;               // Est-ce qu’il existe un PieceJointeStagiaire ?
    private String statut;                  // Validé / En attente / Refusé / null si pas transmis
    private String fichier;                 // URL ou nom du fichier transmis (si existant)
}
