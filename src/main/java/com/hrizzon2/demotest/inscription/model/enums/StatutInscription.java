package com.hrizzon2.demotest.inscription.model.enums;

import lombok.Getter;

@Getter
public enum StatutInscription {

    EN_ATTENTE("En attente"),
    VALIDEE("Validée"),
    REFUSEE("Rejetée"),
    ANNULEE("Annulée"),
    TERMINEE("Terminée"),
    EN_COURS("En cours");

    private final String label;

    StatutInscription(String label) {
        this.label = label;
    }

}

