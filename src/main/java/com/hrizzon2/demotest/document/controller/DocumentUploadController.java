package com.hrizzon2.demotest.document.controller;

import com.hrizzon2.demotest.document.dao.DocumentMongoDao;
import com.hrizzon2.demotest.document.model.AuditAction;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.DocumentMongo;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.document.service.DocumentManagementService;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.formation.service.FormationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

//✅ Gestion MongoDB - Stockage de métadonnées et audit
//✅ Upload sur disque - Sauvegarde directe des fichiers
//✅ Fonctionnement dégradé - Continue même si MongoDB est indisponible

@RestController
@RequestMapping("/documents")
public class DocumentUploadController {

    private final DocumentMongoDao documentMongoDao;
    private final FormationService formationService;
    private final DocumentManagementService documentManagementService;


    public DocumentUploadController(
            @Autowired(required = false)
            DocumentMongoDao documentMongoDao,
            FormationService formationService,
            DocumentManagementService documentManagementService) {
        this.documentMongoDao = documentMongoDao;
        this.formationService = formationService;
        this.documentManagementService = documentManagementService;
    }

    /**
     * Upload pour une formation - Upload principal avec MongoDB
     * Point d'entrée unique pour l'upload de documents liés aux formations
     */
    @PostMapping("/formations/{formationId}/upload")
    public ResponseEntity<?> uploadDocumentForFormation(
            @PathVariable Integer formationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") TypeDocument type,
            @RequestParam("userId") Integer userId
    ) {
        try {
            Formation formation = formationService.findById(formationId)
                    .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée"));

            Document document = documentManagementService.uploadDocument(userId, file, type, formation);

            return ResponseEntity.ok(Map.of(
                    "message", "Document envoyé avec succès !",
                    "documentId", document.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Upload simple (fallback MongoDB optionnel)
     * Pour compatibilité ou cas d'usage spéciaux
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileFallback(@RequestParam("file") MultipartFile file,
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
