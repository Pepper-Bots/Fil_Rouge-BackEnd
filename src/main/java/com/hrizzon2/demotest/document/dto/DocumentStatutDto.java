package com.hrizzon2.demotest.document.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO utilisé dans StagiaireDashboardDto

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentStatutDto {

    private String nomFichier;
    private String type;
    private String statut; // Validé, En attente, etc.
    private String urlFichier;
}
