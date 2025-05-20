package com.hrizzon2.demotest.view;
// (peu utile avec Angular car pas de Thymeleaf)

// TODO A demander si bonne pratique

/**
 * Vues utilisées pour le filtrage JSON avec @JsonView.
 * Permet de contrôler quelles données sont exposées selon le contexte.
 */
public class AffichageDossier {

    // Vue minimale, utilisée pour afficher un résumé du dossier
    public interface Dossier {
    }

    // Vue utilisée pour afficher les infos du stagiaire lié
    public interface Stagiaire extends Dossier {
    }

    // Vue utilisée pour afficher les infos de la formation liée
    public interface Formation extends Dossier {
    }

    // Vue utilisée pour afficher les infos de l’admin créateur
    public interface Admin extends Dossier {
    }

    // Vue complète, combinant tout
    public interface Complet extends Stagiaire, Formation, Admin {
    }
}


// faire un heritage ici
// -> affichage pour admin (voit et modifie tous les dossiers) != affichage pour stagiaire (voit et modifie uniquement son dossier)
// créer 2 affichages dossier !=

// a faire pour stagiaire aussi -> différent traitement -> se rappeler qu'un stagiaire peut avoir plusieurs dossiers d'inscription

