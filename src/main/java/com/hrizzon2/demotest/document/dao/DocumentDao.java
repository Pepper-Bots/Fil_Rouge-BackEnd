package com.hrizzon2.demotest.document.dao;

import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.StatutDocument;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Tu continues à utiliser MySQL/PostgreSQL pour ta gestion “métier” des dossiers/documents (logique métier, liens avec Stagiaire, Dossier, Statut, etc).
// DocumentDao va continuer à gérer tout ce qui concerne la logique relationnelle, la validation, la complétude, etc.
// DocumentDao (JPA) = logique métier “structurée”, tout ce qui a besoin d’être en SQL/transactionnel/lié à d’autres entités.

@Repository
public interface DocumentDao extends JpaRepository<Document, Integer> {

    /**
     * Renvoie tous les Document liés à un dossier dont le stagiaire a l’ID donné.
     * Hibernate générera automatiquement une requête
     * JOIN dossier → stagiaire → WHERE stagiaire.id = :stagiaireId.
     */
    List<Document> findByDossierStagiaireId(Integer stagiaireId);

    /**
     * Renvoie tous les Document d’un type précis pour un même stagiaire (depuis le dossier associé).
     * Exemple d’usage : vérifier qu’on n’a pas déjà soumis un CV ou une pièce d’identité.
     */
    List<Document> findByDossierStagiaireIdAndType(Integer stagiaireId, TypeDocument type);

    /**
     * Pour lister tous les documents dont le statut (StatutDocument.nom) = « EN_ATTENTE ».
     * On peut exploiter la signature automatique si StatutDocument est une entité
     * avec un champ « nom ». Spring Data JPA va comprendre qu’il faut
     * faire un JOIN sur Document → StatutDocument → WHERE nom = :statut.
     */
    List<Document> findByStatutNom(String nom);

    List<Document> findByEvenementId(Integer evenementId);

    int countByStatut(StatutDocument statut);

    List<Document> findByStatut(StatutDocument statut);


    @Query("SELECT d FROM Document d WHERE d.stagiaire.id = :stagiaireId AND d.formation.id = :formationId")
    List<Document> findByStagiaireIdAndFormationId(
            @Param("stagiaireId") Integer stagiaireId,
            @Param("formationId") Integer formationId);

    @Query("SELECT d FROM Document d WHERE d.stagiaire.id = :stagiaireId AND d.formation.id = :formationId AND d.dossier IS NOT NULL")
    List<Document> findDocumentsDossierByStagiaireAndFormation(
            @Param("stagiaireId") Integer stagiaireId,
            @Param("formationId") Integer formationId);
}
