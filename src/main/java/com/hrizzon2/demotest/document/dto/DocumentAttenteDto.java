package com.hrizzon2.demotest.document.dto;

import lombok.Getter;
import lombok.Setter;

// Documents en attente admin ✅

@Getter
@Setter
public class DocumentAttenteDto {

    private String nomFichier;
    private String nomStagiaire;
    private String urlFichier;
}
