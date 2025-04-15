package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class EtatDossier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Enumerated(EnumType.STRING)
    private Status status; // Meilleure scalabilité

    public enum Status {
        COMPLET,
        INCOMPLET,
        VALIDE,
        A_VALIDER
    }
}
