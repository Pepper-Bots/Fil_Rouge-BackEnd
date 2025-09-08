package com.hrizzon2.demotest.document.dao;

import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.StatutDocument;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO pour la gestion des entités Document.
 * Toutes les requêtes sont sécurisées via paramètres bindés (JPQL ou Spring Data).
 * Protection contre les injections SQL par utilisation de @Param et méthodes dérivées.
 */
@Repository
public interface DocumentDao extends JpaRepository<Document, Integer> {

    /**
     * Récupère tous les documents d'un stagiaire via son dossier.
     * Requête sécurisée par paramètre bindé automatique Spring Data.
     *
     * @param stagiaireId Identifiant du stagiaire
     * @return Liste des documents associés au stagiaire
     */
    List<Document> findByDossierStagiaireId(Integer stagiaireId);

    /**
     * Trouve les documents d'un type spécifique pour un stagiaire.
     * Évite la duplication de documents (ex: plusieurs CV).
     * Paramètres automatiquement sécurisés par Spring Data JPA.
     *
     * @param stagiaireId Identifiant du stagiaire
     * @param type        Type de document recherché
     * @return Liste des documents correspondant aux critères
     */
    List<Document> findByDossierStagiaireIdAndTypeDocument(Integer stagiaireId, TypeDocument type);

    /**
     * Récupère tous les documents ayant un statut donné.
     * Utilise la jointure automatique Spring Data vers StatutDocument.
     *
     * @param nom Nom du statut (ex: "EN_ATTENTE", "VALIDÉ")
     * @return Liste des documents avec ce statut
     */
    List<Document> findByStatutNom(String nom);

    /**
     * Trouve les documents liés à un événement spécifique.
     * Paramètre bindé pour éviter l'injection SQL.
     *
     * @param evenementId Identifiant de l'événement
     * @return Liste des documents associés à l'événement
     */
    List<Document> findByEvenementId(Integer evenementId);

    /**
     * Compte le nombre de documents par statut.
     * Méthode sécurisée par Spring Data JPA.
     *
     * @param statut Statut à compter
     * @return Nombre de documents avec ce statut
     */
    int countByStatut(StatutDocument statut);

    /**
     * Récupère tous les documents d'un statut donné.
     *
     * @param statut Statut recherché
     * @return Liste des documents
     */
    List<Document> findByStatut(StatutDocument statut);

    /**
     * Requête JPQL personnalisée avec paramètres bindés.
     * Protection anti-injection via @Param sur les paramètres d'entrée.
     *
     * @param stagiaireId Identifiant du stagiaire
     * @param formationId Identifiant de la formation
     * @return Documents du stagiaire pour cette formation
     */
    @Query("SELECT d FROM Document d WHERE d.stagiaire.id = :stagiaireId AND d.formation.id = :formationId")
    List<Document> findByStagiaireIdAndFormationId(
            @Param("stagiaireId") Integer stagiaireId,
            @Param("formationId") Integer formationId);

    /**
     * Trouve les documents de dossier d'inscription pour un stagiaire et une formation.
     * Requête JPQL sécurisée avec validation que le dossier existe.
     *
     * @param stagiaireId Identifiant du stagiaire
     * @param formationId Identifiant de la formation
     * @return Documents d'inscription du stagiaire
     */
    @Query("SELECT d FROM Document d WHERE d.stagiaire.id = :stagiaireId AND d.formation.id = :formationId AND d.dossier IS NOT NULL")
    List<Document> findDocumentsDossierByStagiaireAndFormation(
            @Param("stagiaireId") Integer stagiaireId,
            @Param("formationId") Integer formationId);
}
