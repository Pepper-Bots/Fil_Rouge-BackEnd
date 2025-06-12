package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.StatutDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutDocumentDao extends JpaRepository<StatutDocument, Integer> {

    /**
     * Trouve un StatutDocument par son nom (ex. « EN_ATTENTE », « VALIDÉ », « REJETÉ »).
     */
    Optional<StatutDocument> findByNom(String nom);

}


// TODO **Conserver** :
//  récupérer statuts document (ex: "VALIDÉ", "EN\_ATTENTE", "REJETÉ").