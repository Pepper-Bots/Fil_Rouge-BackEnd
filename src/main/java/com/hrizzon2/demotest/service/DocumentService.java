package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.dao.DocumentDao;
import com.hrizzon2.demotest.dao.StatutDocumentDao;
import com.hrizzon2.demotest.model.Document;
import com.hrizzon2.demotest.model.StatutDocument;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DocumentService {

    private final DocumentDao documentDao;
    private final FichierService fichierService;
    private final StatutDocumentDao statutDocumentDao;


    @Autowired
    public DocumentService(DocumentDao documentDao, FichierService fichierService, StatutDocumentDao statutDocumentDao) {
        this.documentDao = documentDao;
        this.fichierService = fichierService;
        this.statutDocumentDao = statutDocumentDao;
    }

    public void uploadDocument(Long stagiaireId, MultipartFile fichier, String typeDocument) throws IOException {
        // Nettoyage du nom de fichier
        String safeName = fichierService.sanitizeFileName(fichier.getOriginalFilename());

        // Stockage sur disque
        fichierService.uploadToLocalFileSystem(fichier, safeName, false);

        // Création de l’entité Document avec statut EN_ATTENTE
        Document document = new Document();

        // Si tu as une relation objet:
        // Stagiaire stagiaire = stagiaireDao.findById(stagiaireId).orElseThrow();
        // document.setStagiaire(stagiaire);

        // Si tu stockes l'id simplement:
        document.setStagiaireId(stagiaireId);

        document.setNomFichier(safeName); // ou .setCheminFichier, selon ton modèle
        document.setType(TypeDocument.valueOf(typeDocument)); // à adapter
        document.setStatut(StatutDocument.EN_ATTENTE);

        // Initialisation des autres champs?
        // document.setDateCreation(new Date());

        documentDao.save(document);
    }
}
