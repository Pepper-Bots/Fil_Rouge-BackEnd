package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.StatutDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatutDocumentDao extends JpaRepository<StatutDocument, Integer> {

    // Si tu utilises une entité pour les statuts (donc stockés en BDD), tu dois pouvoir faire des requêtes :
    //“Donne-moi l’id du statut 'À fournir', 'Validé', etc.”
    StatutDocument findByNom(String nom);

}
