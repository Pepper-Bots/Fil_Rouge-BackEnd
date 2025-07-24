package com.hrizzon2.demotest.view;

/**
 * Interfaces de vues pour le filtrage JSON des documents obligatoires.
 * <p>
 * Cette classe définit les niveaux de visibilité pour les données des documents
 * obligatoires associés aux formations. Permet de contrôler l'exposition des
 * informations selon le contexte d'utilisation (liste résumée vs détails complets).
 * </p>
 *
 * @author Votre nom
 * @version 1.0
 * @see AffichageDossier
 * @see com.fasterxml.jackson.annotation.JsonView
 * @since 1.0
 */
public class AffichageListeDocumentsObligatoires {

    /**
     * Vue résumée pour l'affichage simplifié des documents obligatoires.
     * <p>
     * Cette interface expose uniquement les informations essentielles
     * d'un document obligatoire, typiquement l'identifiant et le type.
     * Utilisée pour les listes de documents où seules les informations
     * de base sont nécessaires.
     * </p>
     */
    public interface Resume {
    } // Pour l'affichage simplifié (id, type)

    /**
     * Vue complète incluant les informations détaillées du document et de sa formation.
     * <p>
     * Cette vue hérite de {@link Resume} pour les informations de base du document
     * et de {@link AffichageDossier.Formation} pour inclure les détails de la formation
     * associée. Utilisée lorsqu'il est nécessaire d'afficher le contexte complet
     * du document obligatoire.
     * </p>
     */
    public interface Complet extends Resume, AffichageDossier.Formation {
    }
}
