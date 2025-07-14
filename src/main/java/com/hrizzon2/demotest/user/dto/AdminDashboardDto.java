package com.hrizzon2.demotest.user.dto;

import com.hrizzon2.demotest.dto.DocumentAttenteDto;
import com.hrizzon2.demotest.dto.InscriptionAttenteDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminDashboardDto {
    private int nbStagiaires;
    private int nbFormations;
    private int nbIntervenants;
    private int nbDocsAttente;
    private List<InscriptionAttenteDto> inscriptionsEnAttente;
    private List<DocumentAttenteDto> docsAttente;

    // Getters/Setters
}