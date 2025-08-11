package com.hrizzon2.demotest.document.dto;

import lombok.Getter;
import lombok.Setter;

//validation de document ✅

@Getter
@Setter
public class DocumentStatutUpdateDto {

    private String Statut; // "validé" ou "refusé"

    private String Commentaire; // utilisé si refus

}
