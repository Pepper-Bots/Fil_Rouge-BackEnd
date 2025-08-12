package com.hrizzon2.demotest.document.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Documents en attente admin ✅

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentAttenteDto {

    // Propriétés existantes
    private String nomFichier;
    private String nomStagiaire;
    private String urlFichier;

    // Nouvelles propriétés nécessaires
    private Integer id;
    private String nomFichierOriginal;
    private String typeFichier;
    private String typeDocument;
    private Integer stagiaireId;
    private String prenomStagiaire;
    private String stagiaireEmail;
    private LocalDateTime dateDepot;
    private String statut;
    private Long taille;
    private String cheminFichier;
    private String motifRejet;
    private String validePar;
    private LocalDateTime dateValidation;
}




