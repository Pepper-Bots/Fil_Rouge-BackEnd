package com.hrizzon2.demotest.document.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// TODO Service pas utilisé !!!

@Service
public class DocumentStorageService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    // Stocker le fichier
    public String saveFile(MultipartFile file) throws IOException {
        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        return fileId.toHexString(); // à stocker dans MongoDB dans le champ "cheminStorage"
    }

    // Récupérer le fichier
    public GridFsResource getFile(String fileId) {
        GridFSFile file = gridFsTemplate.findOne(
                org.springframework.data.mongodb.core.query.Query.query(
                        org.springframework.data.mongodb.core.query.Criteria.where("_id").is(fileId)
                )
        );
        return gridFsTemplate.getResource(file);
    }
}
