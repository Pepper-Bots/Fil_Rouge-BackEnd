package com.hrizzon2.demotest.document.controller;

import com.hrizzon2.demotest.document.dao.DocumentMongoDao;
import com.hrizzon2.demotest.document.model.DocumentMongo;
import com.hrizzon2.demotest.model.AuditAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentUploadController {

    // Injection optionnelle
    @Autowired(required = false)
    private DocumentMongoDao documentMongoDao;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("stagiaireId") String stagiaireId,
                                             @RequestParam("nomStagiaire") String nomStagiaire) {
        try {
            // 1. Sauvegarde sur le disque (toujours active)
            String dossier = "uploads/";
            String cheminComplet = dossier + file.getOriginalFilename();
            file.transferTo(new File(cheminComplet));

            // 2. MongoDB optionnel
            if (documentMongoDao != null) {
                // ✅ MongoDB activé - sauvegarde métadonnées
                // 2. Création des métadonnées Mongo
                DocumentMongo doc = new DocumentMongo();
                doc.setNomFichier(file.getOriginalFilename());
                doc.setType(file.getContentType());
                doc.setTaille(file.getSize());
                doc.setDateUpload(new Date());
                doc.setStagiaireId(stagiaireId);
                doc.setNomStagiaire(nomStagiaire);
                doc.setStatut("EN_ATTENTE");
                doc.setCommentaire("En attente de validation par l’admin.");
                doc.setCheminStorage(cheminComplet);

                AuditAction audit = new AuditAction();
                audit.setAction("upload");
                audit.setDate(new Date());
                audit.setPar(nomStagiaire);
                doc.setAudit(List.of(audit));

                // 3. Enregistrement dans MongoDB
                documentMongoDao.save(doc);

                return ResponseEntity.ok("Fichier uploadé et métadonnées MongoDB enregistrées !");
            } else {
                // MongoDB désactivé - fonctionnement dégradé
                return ResponseEntity.ok("Fichier uploadé (MongoDB désactivé - pas de métadonnées)");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l'upload : " + e.getMessage());
        }
    }
}
