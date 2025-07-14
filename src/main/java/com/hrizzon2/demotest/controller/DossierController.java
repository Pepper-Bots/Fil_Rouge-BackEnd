package com.hrizzon2.demotest.controller;
// Contient les endpoints REST

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.dao.FormationDao;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.user.model.Stagiaire;
import com.hrizzon2.demotest.security.AppUserDetails;
import com.hrizzon2.demotest.security.IsAdmin;
import com.hrizzon2.demotest.security.IsStagiaire;
import com.hrizzon2.demotest.service.DossierService;
import com.hrizzon2.demotest.view.AffichageDossier;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// DossierController minimal → simple appel au Service ⇒ Facile à lire et à maintenir
// + simple, + clair / uniquement API
/**
 * Contrôleur REST pour la gestion des dossiers.
 * Suit le principe "contrôleur mince" avec logique déléguée au service.
 */

/// / Afficher les dossiers (liste)
/// / Afficher un dossier (choisi dans la liste)
@CrossOrigin(origins = "${app.cors.origins}", maxAge = 3600)
// Permet les requêtes Cross-Origin (utile pour le frontend séparé du backend)
@RestController // Indique que cette classe est un contrôleur REST
@RequestMapping("/api/dossiers") // Toutes les routes commenceront par /dossiers
public class DossierController {

    private final DossierService dossierService;
    private final FormationDao formationDao;

    // Constructeur avec injection de dépendances via @Autowired
    @Autowired
    public DossierController(DossierService dossierService, FormationDao formationDao) {
        this.dossierService = dossierService;
        this.formationDao = formationDao;
    }

    /**
     * GET /dossiers
     * Retourne tous les dossiers visibles par un stagiaire.
     */
    // Endpoint GET pour récupérer un produit par son id
    // Accessible uniquement aux clients grâce à l’annotation personnalisée @IsStagiaire
    @GetMapping("/dossiers")
    //TODO Méthode à revoir : je n'ai pas besoin que les stagiaires aient accès à tous les dossiers (seulement le leur)
    @IsStagiaire
    @JsonView({AffichageDossier.Dossier.class, AffichageDossier.Stagiaire.class, AffichageDossier.Formation.class})
    public ResponseEntity<List<Dossier>> getAllDossiersVisiblesParStagiaire() {
        List<Dossier> dossiers = dossierService.getAll();
        return new ResponseEntity<>(dossiers, HttpStatus.OK);
    }

    /**
     * GET /api/dossiers/paginated
     * Récupère les dossiers avec pagination (accessible aux stagiaires). // TODO à revoir aussi -> accès stagiaires
     */
    @GetMapping("/paginated")
    @IsStagiaire
    @JsonView({AffichageDossier.Dossier.class, AffichageDossier.Stagiaire.class, AffichageDossier.Formation.class})
    public ResponseEntity<List<Dossier>> getDossiersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        List<Dossier> dossiers = dossierService.findDossiersPaginated(pageable);
        return ResponseEntity.ok(dossiers);
    }

    /**
     * GET /dossiers/{id}
     * Retourne un dossier spécifique.
     * Récupère un dossier spécifique par son ID (accessible aux stagiaires).
     */
    // Endpoint GET pour récupérer tous les dossiers
    // Accessible aux stagiaires
    @GetMapping("/dossier/{id}")
    @IsStagiaire
    @JsonView({AffichageDossier.Dossier.class, AffichageDossier.Stagiaire.class,
            AffichageDossier.Formation.class, AffichageDossier.Admin.class})
    public ResponseEntity<Dossier> getDossierById(@PathVariable int id) {
        try {
            Dossier dossier = dossierService.getById(id);
            // Sinon, on retourne le produit avec un code 200 OK
            return ResponseEntity.ok(dossier);
        } catch (EntityNotFoundException ex) {
            // Si aucun produit trouvé, on retourne un code 404.
            return ResponseEntity.notFound().build(); // Autre écriture -> pourquoi ?
        }
    }

    /**
     * POST /dossiers
     * Crée un nouveau dossier (admin uniquement).
     */
    @PostMapping("/admin")
    @IsAdmin
    @JsonView(AffichageDossier.Dossier.class)
    public ResponseEntity<Dossier> createDossierAsAdmin(@Valid @RequestBody Dossier dossier,
                                                        @AuthenticationPrincipal AppUserDetails userDetails) {

        // Vérifie la formation (comme avant)
        if (dossier.getFormation() == null || dossier.getFormation().getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Formation formation = formationDao.findById(dossier.getFormation().getId())
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " +
                        dossier.getFormation().getId()));

        // On vérifie aussi le stagiaire (important !)
        if (dossier.getStagiaire() == null || dossier.getStagiaire().getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // On peut récupérer l'entité stagiaire complète si besoin (sinon tu passes juste l'ID dans le service)
        // Stagiaire stagiaire = stagiaireDao.findById(dossier.getStagiaire().getId()).orElseThrow(...);

        Dossier created = dossierService.createWithRequiredDocuments(
                dossier.getStagiaire(),
                formation,
                userDetails // C'est bien l'admin qui crée
        );
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * POST /dossiers
     * Crée un nouveau dossier (stagiaire uniquement).
     */
    @PostMapping("/self")
    @IsStagiaire
    @JsonView(AffichageDossier.Dossier.class)
    public ResponseEntity<Dossier> createDossierAsStagiaire(@Valid @RequestBody Dossier dossier,
                                                            @AuthenticationPrincipal AppUserDetails userDetails) {

        // Vérifie la formation
        if (dossier.getFormation() == null || dossier.getFormation().getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Formation formation = formationDao.findById(dossier.getFormation().getId())
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " +
                        dossier.getFormation().getId()));

        // Le stagiaire courant est toujours l'utilisateur connecté
        Stagiaire stagiaireCourant = (Stagiaire) userDetails.getUser();

        Dossier created = dossierService.createWithRequiredDocuments(
                stagiaireCourant,
                formation,
                userDetails // Ici, userDetails = stagiaire
        );
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * PUT /dossiers/{id}
     * Met à jour un dossier (admin uniquement).
     */
    @PutMapping("/dossier/{id}")
    @IsAdmin
    @JsonView(AffichageDossier.Dossier.class)
    public ResponseEntity<Dossier> updateDossier(@PathVariable int id,
                                                 @RequestBody @Valid Dossier dossier,
                                                 @AuthenticationPrincipal AppUserDetails userDetails) {

        try {
            // Vérifie si la formation existe lorsqu'elle est spécifiée
            if (dossier.getFormation() != null && dossier.getFormation().getId() != null) {
                Formation formation = formationDao.findById(dossier.getFormation().getId())
                        .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " +
                                dossier.getFormation().getId()));
                dossier.setFormation(formation);
            }

            // Délègue la mise à jour au service
            Dossier updated = dossierService.update(id, dossier, userDetails);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // TODO Pourquoi <Void> ?

    /**
     * DELETE /dossiers/{id}
     * Supprime un dossier (admin uniquement).
     */
    // Endpoint DELETE pour supprimer un dossier
    // Accessible uniquement aux stagiaires
    @DeleteMapping("/dossier/{id}")
    @IsAdmin
    public ResponseEntity<Void> deleteDossier(
            @PathVariable int id,
            @AuthenticationPrincipal AppUserDetails userDetails) {

        try {
            // Délègue la suppression au service
            dossierService.delete(id, userDetails);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}

// TODO - getDossiers, createDossier, updateDossier, deleteDossier
//  Conserver
//  Gestion des dossiers d’inscription