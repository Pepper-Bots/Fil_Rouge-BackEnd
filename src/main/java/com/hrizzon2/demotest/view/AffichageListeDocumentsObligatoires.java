package com.hrizzon2.demotest.view;

public class AffichageListeDocumentsObligatoires {

    public interface Resume {
    } // Pour l'affichage simplifié (id, type)

    public interface Complet extends Resume, AffichageDossier.Formation {
    }
}
