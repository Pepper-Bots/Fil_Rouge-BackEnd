package com.hrizzon2.demotest.document.dto;

import lombok.Getter;
import lombok.Setter;

//validation de document ✅

@Getter
@Setter
public class DocumentStatutUpdateDto {

    private String statut; // "validé" ou "refusé"

    private String commentaire; // utilisé si refus

}
