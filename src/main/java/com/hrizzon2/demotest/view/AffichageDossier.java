package com.hrizzon2.demotest.view;
// (peu utile avec Angular car pas de Thymeleaf)

// TODO A demander si bonne pratique

/**
 * Interfaces de vues pour le filtrage JSON avec {@code @JsonView}.
 * <p>
 * Cette classe définit différents niveaux de visibilité pour les données des dossiers
 * selon le contexte d'utilisation et les droits d'accès de l'utilisateur.
 * Utilisée en conjonction avec l'annotation {@code @JsonView} de Jackson
 * pour contrôler précisément quelles propriétés sont sérialisées dans les réponses JSON.
 * </p>
 *
 * <h3>Exemple d'utilisation :</h3>
 * <pre>
 * {@code @JsonView(AffichageDossier.Dossier.class)}
 * {@code @GetMapping("/dossiers")}
 * public List&lt;Dossier&gt; getDossiers() { ... }
 * </pre>
 *
 * @author Votre nom
 * @version 1.0
 * @see com.fasterxml.jackson.annotation.JsonView
 * @since 1.0
 */
public class AffichageDossier {

    /**
     * Vue minimale pour l'affichage d'un résumé du dossier.
     * <p>
     * Cette interface sert de vue de base et inclut uniquement les informations
     * essentielles du dossier (identifiant, statut, dates principales, etc.).
     * Utilisée typiquement pour les listes de dossiers ou les aperçus rapides.
     * </p>
     */
    public interface Dossier {
    }

    /**
     * Vue incluant les informations du stagiaire associé au dossier.
     * <p>
     * Hérite de la vue {@link Dossier} et ajoute les données personnelles
     * du stagiaire (nom, prénom, email, etc.). Cette vue est utilisée
     * lorsqu'il est nécessaire d'afficher les détails du candidat
     * en plus des informations de base du dossier.
     * </p>
     */
    public interface Stagiaire extends Dossier {
    }

    /**
     * Vue incluant les informations de la formation liée au dossier.
     * <p>
     * Hérite de la vue {@link Dossier} et ajoute les détails de la formation
     * (nom, description, dates, prérequis, etc.). Utilisée pour afficher
     * le contexte de formation du dossier de candidature.
     * </p>
     */
    public interface Formation extends Dossier {
    }

    /**
     * Vue incluant les informations de l'administrateur créateur du dossier.
     * <p>
     * Hérite de la vue {@link Dossier} et ajoute les données de l'administrateur
     * qui a créé ou modifié le dossier. Utile pour la traçabilité et
     * les fonctionnalités d'audit.
     * </p>
     */
    public interface Admin extends Dossier {
    }

    /**
     * Vue complète combinant toutes les informations disponibles.
     * <p>
     * Cette vue hérite de toutes les autres vues ({@link Stagiaire}, {@link Formation}, {@link Admin})
     * et permet d'exposer l'intégralité des données du dossier. Réservée généralement
     * aux utilisateurs ayant des privilèges élevés ou pour les détails complets d'un dossier.
     * </p>
     */
    public interface Complet extends Stagiaire, Formation, Admin {
    }
}


// faire un heritage ici
// -> affichage pour admin (voit et modifie tous les dossiers) != affichage pour stagiaire (voit et modifie uniquement son dossier)
// créer 2 affichages dossier !=

// a faire pour stagiaire aussi -> différent traitement -> se rappeler qu'un stagiaire peut avoir plusieurs dossiers d'inscription

