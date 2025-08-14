package com.hrizzon2.demotest.document.controller;

import com.hrizzon2.demotest.document.dao.DocumentMongoDao;
import com.hrizzon2.demotest.document.model.AuditAction;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.DocumentMongo;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.document.service.DocumentManagementService;
import com.hrizzon2.demotest.formation.dto.FormationAvecStatutDto;
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

    // Injection optionnelle
    @Autowired(required = false)
    private DocumentMongoDao documentMongoDao;

    @Autowired
    private FormationService formationService;

    @Autowired
    private DocumentManagementService documentManagementService;

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

    // Ajouter dans DocumentUploadController


    /**
     * ✅ Upload pour une formation - ADAPTÉ à tes services existants
     */
    @PostMapping("/formations/{formationId}/upload")
    public ResponseEntity<?> uploadDocumentForFormation(
            @PathVariable Integer formationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") TypeDocument type,
            @RequestParam("userId") Integer userId
    ) {
        try {
            // Utiliser ton service existant DocumentManagementService
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
     * ✅ Récupérer formations d'un stagiaire - UTILISE ton service existant
     */
    @GetMapping("/stagiaire/{userId}/formations")
    public ResponseEntity<List<Formation>> getFormationsByStagiaire(@PathVariable Integer userId) {
        try {
            // Tu as déjà cette méthode dans FormationService !
            List<Formation> formations = formationService.findFormationsByStagiaire(userId);
            return ResponseEntity.ok(formations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * ✅ Documents requis pour une formation - UTILISE ton service existant
     */
    @GetMapping("/formation/{formationId}/documents-requis")
    public ResponseEntity<List<TypeDocument>> getDocumentsRequisFormation(@PathVariable Integer formationId) {
        try {
            Formation formation = formationService.findById(formationId)
                    .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée"));

            // Tu as déjà cette méthode dans Formation.getListeDocumentsObligatoires() !
            List<TypeDocument> typesRequis = formation.getListeDocumentsObligatoires();

            return ResponseEntity.ok(typesRequis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * ✅ Statut dossier formation - UTILISE FormationAvecStatutDto existant
     */
    @GetMapping("/formation/{formationId}/statut/{userId}")
    public ResponseEntity<FormationAvecStatutDto> getStatutDossierFormation(
            @PathVariable Integer formationId,
            @PathVariable Integer userId
    ) {
        try {
            // Tu as déjà cette méthode dans FormationService !
            FormationAvecStatutDto statut = formationService.getStatutDossierFormation(userId, formationId);
            return ResponseEntity.ok(statut);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Ajouter dans DocumentUploadController

    /**
     * ✅ Récupérer toutes les formations avec statut documents
     */
    @GetMapping("/stagiaire/{userId}/formations-avec-statut")
    public ResponseEntity<List<FormationAvecStatutDto>> getFormationsAvecStatutDocuments(@PathVariable Integer userId) {
        try {
            // Tu as déjà cette méthode dans FormationService !
            List<FormationAvecStatutDto> formations = formationService.getFormationsAvecStatutDocuments(userId);
            return ResponseEntity.ok(formations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
