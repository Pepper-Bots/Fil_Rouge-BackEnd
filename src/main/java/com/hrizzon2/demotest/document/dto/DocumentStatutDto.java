package com.hrizzon2.demotest.document.dto;

import lombok.Getter;
import lombok.Setter;

// TODO utilisé dans StagiaireDashboardDto

@Getter
@Setter
public class DocumentStatutDto {

    private String nomFichier;
    private String type;
    private String statut; // Validé, En attente, etc.
    private String urlFichier;
}
