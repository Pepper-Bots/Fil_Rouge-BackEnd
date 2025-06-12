package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Document;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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

//    Optional<Document> findDocumentByName(String name);
//
//    List<Document> findByStagiaireId(Long stagiaireId);
//
//    List<Document> findByStagiaireIdAndType(Long stagiaireId, TypeDocument type);
//
//    List<Document> findByStatut(StatutDocument statut);


}

// TODO **Conserver** :
//  gérer Documents, incluant liens vers Stagiaire, Dossier, Evenement, StatutDocument.
//  Requête pour documents par stagiaire, dossier, évènement.