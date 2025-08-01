package com.hrizzon2.demotest.document.service;

import com.hrizzon2.demotest.document.dao.DocumentMongoDao;
import com.hrizzon2.demotest.document.model.DocumentMongo;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

// TODO Service pas utilisé !!!

@Service
public class DocumentStorageService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private DocumentMongoDao documentMongoDao;

    // Stocker le fichier
    public String saveFile(MultipartFile file, String stagiaireId, String nomStagiaire) throws IOException {
        // Stockage GridFS
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );

        // Création DocumentMongo synchronisée
        DocumentMongo docMongo = new DocumentMongo();
        docMongo.setId(fileId.toHexString());
        docMongo.setNomFichier(file.getOriginalFilename());
        docMongo.setTaille(file.getSize());
        docMongo.setDateUpload(new Date());
        docMongo.setStagiaireId(stagiaireId);
        docMongo.setNomStagiaire(nomStagiaire);
        docMongo.setStatut("EN_ATTENTE");
        docMongo.setCheminStorage(fileId.toHexString());

        documentMongoDao.save(docMongo);

        return fileId.toHexString(); // à stocker dans MongoDB dans le champ "cheminStorage"
    }

    // Récupérer le fichier
    public GridFsResource getFile(String fileId) {
        try {
            ObjectId objectId = new ObjectId(fileId);
            GridFSFile file = gridFsTemplate.findOne(
                    Query.query(Criteria.where("_id").is(objectId))
            );

            if (file == null) {
                throw new FileNotFoundException("Fichier non trouvé : " + fileId);
            }

            return gridFsTemplate.getResource(file);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ID de fichier invalide : " + fileId);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String fileId) {
        try {
            ObjectId objectId = new ObjectId(fileId);
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(objectId)));
            documentMongoDao.deleteById(fileId);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression : " + fileId, e);
        }
    }
}
