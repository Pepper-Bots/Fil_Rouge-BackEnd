package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.dao.DocumentDao;
import com.hrizzon2.demotest.dao.DossierDao;
import com.hrizzon2.demotest.dao.StatutDocumentDao;
import com.hrizzon2.demotest.model.Document;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.model.StatutDocument;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service pour la gestion des documents rattachés à un dossier (ancienne logique).
 */
@Service
public class DocumentService {

    private final DocumentDao documentDao;
    private final DossierDao dossierDao;
    private final FichierService fichierService;
    private final StatutDocumentDao statutDocumentDao;

    @Autowired
    public DocumentService(DocumentDao documentDao, DossierDao dossierDao, FichierService fichierService, StatutDocumentDao statutDocumentDao) {
        this.documentDao = documentDao;
        this.dossierDao = dossierDao;
        this.fichierService = fichierService;
        this.statutDocumentDao = statutDocumentDao;
    }

    /**
     * Upload un document lié à un dossier existant.
     *
     * @param dossierId    Id du dossier auquel rattacher le document
     * @param fichier      Fichier à uploader
     * @param typeDocument Type du document (nom de l'enum)
     */
    public void uploadDocument(Integer dossierId, MultipartFile fichier, String typeDocument) throws IOException {
        String safeName = fichierService.sanitizeFileName(fichier.getOriginalFilename());
        fichierService.uploadToLocalFileSystem(fichier, safeName, false);

        Dossier dossier = dossierDao.findById(dossierId)
                .orElseThrow(() -> new IllegalArgumentException("Dossier non trouvé pour l'id " + dossierId));

        StatutDocument statutEnAttente = statutDocumentDao.findByNom("EN_ATTENTE")
                .orElseThrow(() -> new IllegalStateException("Statut EN_ATTENTE introuvable en base !"));

        Document document = new Document();
        document.setDossier(dossier);
        document.setNomFichier(safeName);
        document.setType(TypeDocument.valueOf(typeDocument));
        document.setStatut(statutEnAttente);

        documentDao.save(document);
    }
}
