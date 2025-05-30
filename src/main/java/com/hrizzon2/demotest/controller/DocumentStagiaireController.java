package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.dao.DocumentStagiaireDao;
import com.hrizzon2.demotest.dto.DocumentStatutDTO;
import com.hrizzon2.demotest.model.DocumentStagiaire;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import com.hrizzon2.demotest.service.DocumentStagiaireService;
import com.hrizzon2.demotest.service.FormationService;
import com.hrizzon2.demotest.service.stagiaire.StagiaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des documents transmis par les stagiaires.
 */
@RestController
@RequestMapping("/stagiaires")
public class DocumentStagiaireController {

    private final DocumentStagiaireDao documentStagiaireDao;
    private final FormationService formationService;
    private final StagiaireService stagiaireService;
    private final DocumentStagiaireService documentStagiaireService;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param documentStagiaireDao     DAO pour l'accès aux documents stagiaires.
     * @param formationService         Service pour l'accès aux formations.
     * @param stagiaireService         Service pour l'accès aux stagiaires.
     * @param documentStagiaireService Service métier pour la logique de comparaison attendus/transmis.
     */
    @Autowired
    public DocumentStagiaireController(DocumentStagiaireDao documentStagiaireDao,
                                       FormationService formationService,
                                       StagiaireService stagiaireService, DocumentStagiaireService documentStagiaireService) {
        this.documentStagiaireDao = documentStagiaireDao;
        this.formationService = formationService;
        this.stagiaireService = stagiaireService;
        this.documentStagiaireService = documentStagiaireService;
    }

    /**
     * Upload d'un document transmis par un stagiaire pour une formation.
     *
     * @param stagiaireId ID du stagiaire
     * @param payload     Informations sur le document à transmettre
     * @return Le document enregistré
     */
    @PostMapping("/{stagiaireId}/documents")
    public ResponseEntity<DocumentStagiaire> uploadDocument(
            @PathVariable Integer stagiaireId,
            @RequestBody DocumentPayload payload) {
        Stagiaire stagiaire = stagiaireService.findById(stagiaireId)
                .orElseThrow(() -> new RuntimeException("Stagiaire non trouvé"));
        Formation formation = formationService.findById(payload.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation non trouvée"));

        DocumentStagiaire doc = new DocumentStagiaire();
        doc.setStagiaire(stagiaire);
        doc.setFormation(formation);
        doc.setTypeDocument(payload.getTypeDocument());
        doc.setFichier(payload.getFichier());

        DocumentStagiaire saved = documentStagiaireDao.save(doc);
        return ResponseEntity.status(201).body(saved);
    }

    /**
     * Liste les documents transmis par un stagiaire, optionnellement filtrés par formation.
     *
     * @param stagiaireId ID du stagiaire
     * @param formationId (optionnel) ID de la formation
     * @return Liste des documents transmis
     */
    @GetMapping("/{stagiaireId}/documents")
    public ResponseEntity<List<DocumentStagiaire>> getDocumentsForStagiaireAndFormation(
            @PathVariable Integer stagiaireId,
            @RequestParam(required = false) Integer formationId) {
        List<DocumentStagiaire> docs;
        if (formationId != null) {
            docs = documentStagiaireDao.findByStagiaireIdAndFormationId(stagiaireId, formationId);
        } else {
            docs = documentStagiaireDao.findByStagiaireId(stagiaireId);
        }
        return ResponseEntity.ok(docs);
    }

    /**
     * Supprime un document transmis par un stagiaire.
     *
     * @param stagiaireId ID du stagiaire
     * @param docId       ID du document à supprimer
     * @return HTTP 204 si succès
     */
    @DeleteMapping("/{stagiaireId}/documents/{docId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Integer stagiaireId,
            @PathVariable Integer docId) {
        documentStagiaireDao.deleteById(docId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retourne la liste croisée des documents attendus (par la formation) et transmis (par le stagiaire)
     * avec leur statut (transmis, validé, etc.).
     *
     * @param stagiaireId ID du stagiaire
     * @param formationId ID de la formation
     * @return Liste des statuts de chaque document attendu
     */
    @GetMapping("/{stagiaireId}/formations/{formationId}/statut-documents")
    public ResponseEntity<List<DocumentStatutDTO>> getStatutDocuments(
            @PathVariable Integer stagiaireId,
            @PathVariable Integer formationId) {
        Stagiaire stagiaire = stagiaireService.findById(stagiaireId)
                .orElseThrow(() -> new RuntimeException("Stagiaire non trouvé"));
        Formation formation = formationService.findById(formationId)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée"));
        List<DocumentStatutDTO> statutDocs = documentStagiaireService.getStatutDocumentsDossier(stagiaire, formation);
        return ResponseEntity.ok(statutDocs);
    }

    /**
     * Classe interne pour le payload d'upload d'un document.
     */
    public static class DocumentPayload {
        private Integer formationId;
        private TypeDocument typeDocument;
        private String fichier;

        // getters/setters
        public Integer getFormationId() {
            return formationId;
        }

        public void setFormationId(Integer formationId) {
            this.formationId = formationId;
        }

        public TypeDocument getTypeDocument() {
            return typeDocument;
        }

        public void setTypeDocument(TypeDocument typeDocument) {
            this.typeDocument = typeDocument;
        }

        public String getFichier() {
            return fichier;
        }

        public void setFichier(String fichier) {
            this.fichier = fichier;
        }
    }
}
