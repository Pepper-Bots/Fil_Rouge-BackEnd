package com.hrizzon2.demotest.user.dto;

import com.hrizzon2.demotest.document.dto.DocumentStatutDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StagiaireDashboardDto {
    private String prenom;
    private String nom;
    private int nbDocsValides;
    private int nbDocsEnAttente;
    private String statutDossier;
    private List<DocumentStatutDto> documents;

    // Getters/Setters
}