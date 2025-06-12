package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.service.FormationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/formations")
public class FormationController {

    private final FormationService formationService;

    @Autowired
    public FormationController(FormationService formationService) {
        this.formationService = formationService;
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
        existing.setNom(updatedFormation.getTitre());
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
}
