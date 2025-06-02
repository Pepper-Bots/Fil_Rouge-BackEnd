package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.StatutDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutDocumentDao extends JpaRepository<StatutDocument, Integer> {

    // Si tu utilises une entité pour les statuts (donc stockés en BDD), tu dois pouvoir faire des requêtes :
    //“Donne-moi l’id du statut 'À fournir', 'Validé', etc.”
    Optional<StatutDocument> findByNom(String nom);

}


