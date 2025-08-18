package com.hrizzon2.demotest.formation.controller;

import com.hrizzon2.demotest.document.dto.DocumentSummaryDto;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.formation.dto.FormationAvecStatutDto;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.formation.service.FormationService;
import com.hrizzon2.demotest.user.model.PieceJointeStagiaire;
import com.hrizzon2.demotest.user.model.Stagiaire;
import com.hrizzon2.demotest.user.service.PieceJointeStagiaireService;
import com.hrizzon2.demotest.user.service.Stagiaire.StagiaireService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/formation")
public class FormationController {

    private final FormationService formationService;
    private final StagiaireService stagiaireService;
    private final PieceJointeStagiaireService pieceJointeStagiaireService;

    @Autowired
    public FormationController(FormationService formationService,
                               StagiaireService stagiaireService,
                               PieceJointeStagiaireService pieceJointeStagiaireService) {
        this.formationService = formationService;
        this.stagiaireService = stagiaireService;
        this.pieceJointeStagiaireService = pieceJointeStagiaireService;
    }

    @GetMapping("/formations")
    public ResponseEntity<List<Formation>> getAllFormations() {
        return ResponseEntity.ok(formationService.findAll());
    }

    @GetMapping("/formation/{id}")
    public ResponseEntity<Formation> getFormationById(@PathVariable int id) {
        Optional<Formation> formation = formationService.findById(id);
        return formation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // À ajouter dans FormationController
    @GetMapping("/stagiaire/{stagiaireId}/formations")
    public ResponseEntity<List<Formation>> getFormationsStagiaire(@PathVariable Integer stagiaireId) {
        List<Formation> formations = formationService.findFormationsByStagiaire(stagiaireId);
        return ResponseEntity.ok(formations);
    }

    @PostMapping("/formation")
    public ResponseEntity<Formation> createFormation(@RequestBody @Valid Formation formation) {
        Formation saved = formationService.save(formation);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/formation/{id}")
    public ResponseEntity<Formation> updateFormation(@PathVariable int id, @RequestBody @Valid Formation updatedFormation) {
        Optional<Formation> optionalFormation = formationService.findById(id);
        if (optionalFormation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Formation existing = optionalFormation.get();
        existing.setNom(updatedFormation.getNom());
        existing.setDescription(updatedFormation.getDescription());

        return ResponseEntity.ok(formationService.save(existing));
    }

    @DeleteMapping("/formation/{id}")
    public ResponseEntity<Void> deleteFormation(@PathVariable int id) {
        if (!formationService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        formationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupère les documents requis pour une formation donnée
     */
    @GetMapping("/formation/{formationId}/documents-requis")
    @PreAuthorize("hasAuthority('STAGIAIRE') or hasAuthority('ADMIN')")
    public ResponseEntity<List<TypeDocument>> getDocumentsRequis(@PathVariable Integer formationId) {
        try {
            Optional<Formation> formationOpt = formationService.findById(formationId);
            if (formationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Formation formation = formationOpt.get();
            // Utilise votre méthode @Transient getListeDocumentsObligatoires()
            List<TypeDocument> documentsRequis = formation.getListeDocumentsObligatoires();
            return ResponseEntity.ok(documentsRequis);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Récupère les formations d'un stagiaire avec le statut de ses documents (alimente le tableau/progression)
     */
    @GetMapping("/stagiaire/{stagiaireId}/formations-avec-statut")
    @PreAuthorize("hasAuthority('STAGIAIRE') or hasAuthority('ADMIN')")
    public ResponseEntity<List<FormationAvecStatutDto>> getFormationsAvecStatut(
            @PathVariable Integer stagiaireId) {
        List<FormationAvecStatutDto> formations =
                formationService.getFormationsAvecStatutDocuments(stagiaireId);
        return ResponseEntity.ok(formations);
    }

    /**
     * Récupère le statut de complétion du dossier d'un stagiaire pour une formation donnée
     */
    @GetMapping("/formation/{formationId}/stagiaire/{stagiaireId}/statut-dossier")
    @PreAuthorize("hasAuthority('STAGIAIRE') or hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatutDossierStagiaire(
            @PathVariable Integer formationId,
            @PathVariable Integer stagiaireId) {
        try {
            // Récupérer les documents requis pour cette formation
            Optional<Formation> formationOpt = formationService.findById(formationId);
            if (formationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<TypeDocument> documentsRequis = formationOpt.get().getListeDocumentsObligatoires();

            // Récupérer les documents déjà soumis par le stagiaire
            List<PieceJointeStagiaire> documentsDeposes = pieceJointeStagiaireService
                    .getPiecesPourStagiaireEtFormation(stagiaireId, formationId);

            // Calculer le pourcentage de complétion
            int totalRequis = documentsRequis.size();
            int totalDeposes = documentsDeposes.size();
            double pourcentageCompletion = totalRequis > 0 ? (double) totalDeposes / totalRequis * 100 : 100;

            Map<String, Object> statut = new HashMap<>();
            statut.put("pourcentageCompletion", Math.round(pourcentageCompletion));
            statut.put("documentsRequis", documentsRequis);
            statut.put("documentsDeposes", documentsDeposes);
            statut.put("estComplet", totalDeposes >= totalRequis);

            return ResponseEntity.ok(statut);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Récupère le résumé détaillé des documents pour une formation
     */
    @GetMapping("/formation/{formationId}/stagiaire/{stagiaireId}/documents-summary")
    @PreAuthorize("hasAuthority('STAGIAIRE') or hasAuthority('ADMIN')")
    public ResponseEntity<List<DocumentSummaryDto>> getDocumentsSummary(
            @PathVariable Integer formationId,
            @PathVariable Integer stagiaireId) {
        try {
            Optional<Formation> formationOpt = formationService.findById(formationId);
            Optional<Stagiaire> stagiaireOpt = stagiaireService.findById(stagiaireId); // Il vous faudra ce service aussi

            if (formationOpt.isEmpty() || stagiaireOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<DocumentSummaryDto> summary = pieceJointeStagiaireService
                    .getStatutDocumentsDossier(stagiaireOpt.get(), formationOpt.get());

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
