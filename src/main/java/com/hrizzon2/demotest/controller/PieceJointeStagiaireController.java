package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.annotation.ValidFile;
import com.hrizzon2.demotest.dto.DocumentStatutUpdateDto;
import com.hrizzon2.demotest.dto.DocumentSummaryDto;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.PieceJointeStagiaire;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import com.hrizzon2.demotest.service.FormationService;
import com.hrizzon2.demotest.service.PieceJointeStagiaireService;
import com.hrizzon2.demotest.service.stagiaire.StagiaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur pour la gestion des documents transmis par les stagiaires.
 */
@RestController
@RequestMapping("/stagiaires")
public class PieceJointeStagiaireController {

    private final FormationService formationService;
    private final StagiaireService stagiaireService;
    private final PieceJointeStagiaireService pieceJointeStagiaireService;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param formationService            Service pour l'accès aux formations.
     * @param stagiaireService            Service pour l'accès aux stagiaires.
     * @param pieceJointeStagiaireService Service métier pour la logique de comparaison attendus/transmis.
     */
    @Autowired
    public PieceJointeStagiaireController(FormationService formationService,
                                          StagiaireService stagiaireService,
                                          PieceJointeStagiaireService pieceJointeStagiaireService) {
        this.formationService = formationService;
        this.stagiaireService = stagiaireService;
        this.pieceJointeStagiaireService = pieceJointeStagiaireService;
    }

    /**
     * Upload d'une pièce jointe (document) transmis par un stagiaire pour une formation.
     *
     * @param stagiaireId ID du stagiaire
     * @return Le document enregistré
     */
    @PostMapping("/stagiaire/{stagiaireId}/formation/{formationId}/upload")
    public ResponseEntity<?> uploadPieceJointe(
            @PathVariable Integer stagiaireId,
            @PathVariable Integer formationId,
            @RequestParam("type") String typeDocument,
            @ValidFile @RequestParam("file") MultipartFile file
    ) {
        try {
            Stagiaire stagiaire = stagiaireService.findById(stagiaireId)
                    .orElseThrow(() -> new IllegalArgumentException("Stagiaire introuvable"));
            Formation formation = formationService.findById(formationId)
                    .orElseThrow(() -> new IllegalArgumentException("Formation introuvable"));

            PieceJointeStagiaire piece = pieceJointeStagiaireService.uploadPieceJointe(stagiaire, formation, TypeDocument.valueOf(typeDocument), file);
            return ResponseEntity.status(HttpStatus.CREATED).body(piece);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur d'upload");
        }
    }

    /**
     * Liste toutes les pièces jointes transmises par un stagiaire, optionnellement filtrés par formation.
     *
     * @param stagiaireId ID du stagiaire
     * @param formationId (optionnel) ID de la formation
     * @return Liste des documents transmis
     */
    @GetMapping("/stagiaire/{stagiaireId}/formation/{formationId}")
    public ResponseEntity<List<PieceJointeStagiaire>> getPiecesPourStagiaireEtFormation(
            @PathVariable Integer stagiaireId,
            @PathVariable Integer formationId) {
        List<PieceJointeStagiaire> list = pieceJointeStagiaireService.getPiecesPourStagiaireEtFormation(stagiaireId, formationId);
        return ResponseEntity.ok(list);
    }

    /**
     * Supprime un document transmis par un stagiaire.
     *
     * @return HTTP 204 si succès
     */
    @DeleteMapping("/{pieceId}")
    public ResponseEntity<Void> deletePieceJointe(@PathVariable Integer pieceId) {
        pieceJointeStagiaireService.deletePieceJointe(pieceId);
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
    public ResponseEntity<List<DocumentSummaryDto>> getStatutDocuments(
            @PathVariable Integer stagiaireId,
            @PathVariable Integer formationId) {
        Stagiaire stagiaire = stagiaireService.findById(stagiaireId)
                .orElseThrow(() -> new RuntimeException("Stagiaire non trouvé"));
        Formation formation = formationService.findById(formationId)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée"));
        List<DocumentSummaryDto> statutDocs = pieceJointeStagiaireService.getStatutDocumentsDossier(stagiaire, formation);
        return ResponseEntity.ok(statutDocs);
    }


        @PatchMapping("/documents/{documentId}/statut")
        public ResponseEntity<?> updateDocumentStatut(
                @PathVariable Integer documentId,
                @RequestBody DocumentStatutUpdateDto dto
        ) {
            pieceJointeStagiaireService.updateStatutDocument(documentId, dto);
            return ResponseEntity.ok().build();
        }


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
