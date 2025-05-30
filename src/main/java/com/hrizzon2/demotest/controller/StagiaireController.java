package com.hrizzon2.demotest.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import com.hrizzon2.demotest.security.IsAdmin;
import com.hrizzon2.demotest.service.FormationService;
import com.hrizzon2.demotest.service.stagiaire.StagiaireService;
import com.hrizzon2.demotest.view.AffichageDossier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// le controller s'occupe exclusivement de la réception des requêtes HTTP

/**
 * Contrôleur REST pour la gestion des stagiaires.
 * Fournit des points d’entrée pour les opérations CRUD.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class StagiaireController {

    private final StagiaireService stagiaireService;
    private final FormationService formationService;

    @Autowired
    public StagiaireController(StagiaireService stagiaireService, FormationService formationService) {
        this.stagiaireService = stagiaireService;
        this.formationService = formationService;
    }

    /**
     * Récupère les stagiaires par leur statut
     *
     * @param statut statut du stagiaire
     * @return Stagiaire selon son statut
     */
    @GetMapping("/stagiaires/statut")
    public ResponseEntity<List<Stagiaire>> getStagiairesByStatut(@RequestParam StatutInscription statut) {
        return ResponseEntity.ok(stagiaireService.findByStatutInscription(statut));
    }

    /**
     * Récupère stagiaires par période d'inscription
     *
     * @param debut
     * @param fin
     * @return
     */
    @GetMapping("/stagiaires/inscrits")
    public ResponseEntity<List<Stagiaire>> getStagiairesInscritsEntre(
            @RequestParam LocalDate debut,
            @RequestParam LocalDate fin) {
        return ResponseEntity.ok(stagiaireService.findInscritsEntre(debut, fin));
    }

    /**
     * Récupère tous les stagiaires.
     *
     * @return Liste de tous les stagiaires
     */
    // S'assurer que ton endpoint /stagiaires dans StagiaireController est accessible
    // et retourne une liste d'objets Stagiaire au format JSON.
    @IsAdmin
    @GetMapping("/stagiaires")
    @JsonView(AffichageDossier.Stagiaire.class)
    public ResponseEntity<List<Stagiaire>> getAllStagiaires() {
        return ResponseEntity.ok(stagiaireService.findAll());
    }

    @GetMapping("/stagiaires/complet")
    @JsonView(AffichageDossier.Complet.class)
    public ResponseEntity<List<Stagiaire>> getAllStagiairesComplet() {
        return ResponseEntity.ok(stagiaireService.findAll());
    }

    @GetMapping("/stagiaires/admin")
    @JsonView(AffichageDossier.Admin.class)
    public ResponseEntity<List<Stagiaire>> getAllStagiairesAdmin() {
        return ResponseEntity.ok(stagiaireService.findAll());
    }

    /**
     * Récupère un stagiaire par son identifiant.
     *
     * @param id Identifiant du stagiaire
     * @return Stagiaire trouvé ou code 404
     */
    @GetMapping("/stagiaire/{id}")
    public ResponseEntity<Stagiaire> getStagiaireById(@PathVariable int id) {
        return stagiaireService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/stagiaire/me")
    @JsonView(AffichageDossier.Stagiaire.class)
    public ResponseEntity<Stagiaire> getMonProfil(Authentication authentication) {
        // Récupère le login/email depuis le token JWT ou le principal
        String email = authentication.getName(); // ou .getPrincipal().getUsername() selon config
        return stagiaireService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée un nouveau stagiaire, avec ou sans inscription à une formation.
     *
     * @param stagiaire   Données du stagiaire
     * @param formationId ID de la formation (optionnel)
     * @return Stagiaire créé
     */
    @PostMapping("/stagiaire")
    public ResponseEntity<Stagiaire> createStagiaire(@RequestBody @Valid Stagiaire stagiaire,
                                                     @RequestParam(required = false) Integer formationId) {

        Stagiaire savedStagiaire = stagiaireService.save(stagiaire);  // Sauvegarde via le service

        // Si formationId est fourni, on associe le stagiaire à cette formation
        if (formationId != null) {
            Optional<Formation> formationOpt = formationService.findById(formationId);
            if (formationOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            stagiaireService.inscrireStagiaire(stagiaire, formationOpt.get());
        }
        return new ResponseEntity<>(savedStagiaire, HttpStatus.CREATED);
    }

    /**
     * Met à jour un stagiaire existant.
     *
     * @param id               ID du stagiaire
     * @param updatedStagiaire Données à mettre à jour
     * @return Stagiaire mis à jour ou code 404
     */
    @PutMapping("/stagiaire/{id}")
    public ResponseEntity<Stagiaire> updateStagiaire(@PathVariable int id, @RequestBody @Valid Stagiaire updatedStagiaire) {
        Optional<Stagiaire> optionalStagiaire = stagiaireService.findById(id);
        if (optionalStagiaire.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Stagiaire existing = optionalStagiaire.get();
        existing.setLastName(updatedStagiaire.getLastName());
        existing.setFirstName(updatedStagiaire.getFirstName());
        existing.setEmail(updatedStagiaire.getEmail());

        stagiaireService.save(existing);
        return ResponseEntity.ok(existing);
    }

    /**
     * Supprime un stagiaire par son ID.
     *
     * @param id ID du stagiaire
     * @return Code 204 si supprimé, 404 sinon
     */
    @DeleteMapping("/stagiaire/{id}")
    public ResponseEntity<Void> deleteStagiaire(@PathVariable int id) {
        if (!stagiaireService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stagiaireService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
