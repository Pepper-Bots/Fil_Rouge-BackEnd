package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.service.FormationService;
import com.hrizzon2.demotest.service.StagiaireService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class StagiaireController {

    private final StagiaireService stagiaireService;
    private final FormationService formationService;

    @Autowired
    public StagiaireController(StagiaireService stagiaireService, FormationService formationService) {
        this.stagiaireService = stagiaireService;
        this.formationService = formationService;
    }

    @GetMapping("/stagiaires")
    public ResponseEntity<List<Stagiaire>> getAllStagiaires() {
        return ResponseEntity.ok(stagiaireService.findAll());
    }

    @GetMapping("/stagiaire/{id}")
    public ResponseEntity<Stagiaire> getStagiaireById(@PathVariable int id) {
        return stagiaireService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/stagiaire")
    public ResponseEntity<Stagiaire> createStagiaire(@RequestBody @Valid Stagiaire stagiaire,
                                                     @RequestParam(required = false) Integer formationId) {
        // Si formationId est fourni, on associe le stagiaire à cette formation
        if (formationId != null) {
            Optional<Formation> formationOpt = formationService.findById(formationId);
            if (formationOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            stagiaire = stagiaireService.inscrireStagiaire(stagiaire, formationOpt.get());
        } else {
            stagiaire = stagiaireService.save(stagiaire);
        }

        return new ResponseEntity<>(stagiaire, HttpStatus.CREATED);
    }

    @PutMapping("/stagiaire/{id}")
    public ResponseEntity<Stagiaire> updateStagiaire(@PathVariable int id, @RequestBody @Valid Stagiaire updatedStagiaire) {
        Optional<Stagiaire> optionalStagiaire = stagiaireService.findById(id);
        if (optionalStagiaire.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Stagiaire existing = optionalStagiaire.get();
        existing.setNom(updatedStagiaire.getNom());
        existing.setPrenom(updatedStagiaire.getPrenom());
        existing.setEmail(updatedStagiaire.getEmail());
        existing.setInscription(updatedStagiaire.getInscription()); // ou gestion plus fine

        stagiaireService.save(existing);
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/stagiaire/{id}")
    public ResponseEntity<Void> deleteStagiaire(@PathVariable int id) {
        if (!stagiaireService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stagiaireService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
