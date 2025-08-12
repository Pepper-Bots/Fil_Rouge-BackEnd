package com.hrizzon2.demotest.inscription.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InscriptionAttenteDto {
    private String nomStagiaire;
    private String formationNom;
    private String statutDossier;

    private Integer id;
    private Integer stagiaireId;
    private String prenomStagiaire;
    private String stagiaireEmail;
    private Integer formationId;
    private LocalDateTime dateInscription;
    ;
    private List<String> documentsManquants;
    private Integer documentsDeposes;
    private Integer documentsRequis;
    private String priorite;

}


