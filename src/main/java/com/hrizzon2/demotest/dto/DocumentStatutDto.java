package com.hrizzon2.demotest.dto;

import lombok.Getter;
import lombok.Setter;

// TODO DTO pas utilisé !!!

@Getter
@Setter
public class DocumentStatutDto {

    private String nomFichier;
    private String type;
    private String statut; // Validé, En attente, etc.
    private String urlFichier;
}
