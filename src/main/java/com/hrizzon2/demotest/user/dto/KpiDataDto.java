package com.hrizzon2.demotest.user.dto;

// KpiDataDto.java - À CRÉER
//package com.hrizzon2.demotest.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KpiDataDto {
    private Long nbStagiaires;
    private Long nbFormations;
    private Long nbIntervenants;
    private Long nbDocsAttente;
    private Long nbDocsValidation;
    private Long nbInscriptionsAttente;
    private Double evolutionStagiaires;
    private Double evolutionFormations;
}

