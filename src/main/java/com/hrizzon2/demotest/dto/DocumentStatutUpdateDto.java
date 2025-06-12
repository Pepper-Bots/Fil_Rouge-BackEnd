package com.hrizzon2.demotest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentStatutUpdateDto {

    private String Statut; // "validé" ou "refusé"

    private String Commentaire; // utilisé si refus

}
