package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.dao.EvenementDao;
import com.hrizzon2.demotest.document.dao.DocumentDao;
import com.hrizzon2.demotest.document.dao.StatutDocumentDao;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.StatutDocument;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.model.Evenement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EvenementDocumentService {

    private final DocumentDao documentDao;
    private final EvenementDao evenementDao;
    private final StatutDocumentDao statutDocumentDao;
    private final FichierService fichierService;

    @Autowired
    public EvenementDocumentService(DocumentDao documentDao,
                                    EvenementDao evenementDao,
                                    StatutDocumentDao statutDocumentDao,
                                    FichierService fichierService) {
        this.documentDao = documentDao;
        this.evenementDao = evenementDao;
        this.statutDocumentDao = statutDocumentDao;
        this.fichierService = fichierService;
    }

    public void uploadDocument(Integer evenementId, MultipartFile file, TypeDocument type) {
        try {
            String cleanFileName = fichierService.sanitizeFileName(file.getOriginalFilename());
            fichierService.uploadToLocalFileSystem(file, cleanFileName, false);

            Evenement evenement = evenementDao.findById(evenementId)
                    .orElseThrow(() -> new IllegalArgumentException("Évènement introuvable"));

            StatutDocument statutEnAttente = statutDocumentDao.findByNom("EN_ATTENTE")
                    .orElseThrow(() -> new IllegalStateException("Statut EN_ATTENTE introuvable"));

            Document document = new Document();
            document.setEvenement(evenement);
            document.setNomFichier(cleanFileName);
            document.setType(type);
            document.setStatut(statutEnAttente);
            document.setDateDepot(LocalDateTime.now());

            documentDao.save(document);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier", e);
        }
    }

    public boolean supprimerDocument(Integer documentId) {
        Optional<Document> docOpt = documentDao.findById(documentId);
        if (docOpt.isEmpty()) {
            return false;
        }
        Document doc = docOpt.get();

        try {
            fichierService.deleteFile(doc.getNomFichier());
        } catch (Exception e) {
            // Log erreur, continuer suppression
        }

        documentDao.delete(doc);
        return true;
    }

    public List<Document> getDocumentsByEvenement(Integer evenementId) {
        // Vérifie si l'évènement existe (optionnel mais conseillé)
        if (!evenementDao.existsById(evenementId)) {
            throw new IllegalArgumentException("Évènement introuvable");
        }
        // Rechercher et retourner tous les documents liés à cet évènement
        return documentDao.findByEvenementId(evenementId);
    }
}