package com.hrizzon2.demotest.model.enums;

import lombok.Getter;

@Getter
public enum StatutInscription {

    EN_ATTENTE("En attente"),
    ACCEPTE("Accepté"),
    REFUSE("Refusé"),
    ANNULE("Annulé");

    private final String label;

    StatutInscription(String label) {
        this.label = label;
    }

}
