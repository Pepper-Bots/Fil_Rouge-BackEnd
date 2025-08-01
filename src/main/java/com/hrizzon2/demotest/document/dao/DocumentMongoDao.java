package com.hrizzon2.demotest.document.dao;

import com.hrizzon2.demotest.document.model.DocumentMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//  Stockage complémentaire pour certaines données : métadonnées, logs, éventuellement fichiers eux-mêmes.
// Appeler ton repo Mongo pour stocker les métadonnées :

@Repository
public interface DocumentMongoDao extends MongoRepository<DocumentMongo, String> {

    List<DocumentMongo> findByStagiaireId(String stagiaireId);

    List<DocumentMongo> findByStatut(String statut);

    Optional<DocumentMongo> findByCheminStorage(String cheminStorage);
}
