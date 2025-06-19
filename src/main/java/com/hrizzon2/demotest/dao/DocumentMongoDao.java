package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.DocumentMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

//  stockage complémentaire pour certaines données : métadonnées, logs, éventuellement fichiers eux-mêmes.
// Appeler ton repo Mongo pour stocker les métadonnées :

@Repository
public interface DocumentMongoDao extends MongoRepository<DocumentMongo, String> {

    // Après l’upload et la sauvegarde fichier :
    DocumentMongo docMongo = new DocumentMongo();
    // ... set tous les champs
//    documentMongoDao.save(docMongo);
}
