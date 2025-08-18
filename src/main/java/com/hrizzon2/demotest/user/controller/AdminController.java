package com.hrizzon2.demotest.user.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.service.ValidationDocumentService;
import com.hrizzon2.demotest.evenement.service.EvenementDocumentService;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.formation.service.FormationService;
import com.hrizzon2.demotest.inscription.model.enums.StatutInscription;
import com.hrizzon2.demotest.inscription.service.DossierDocumentService;
import com.hrizzon2.demotest.security.IsAdmin;
import com.hrizzon2.demotest.user.model.Admin;
import com.hrizzon2.demotest.user.model.Stagiaire;
import com.hrizzon2.demotest.user.service.AdminService;
import com.hrizzon2.demotest.user.service.Stagiaire.StagiaireService;
import com.hrizzon2.demotest.view.AffichageDossier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final StagiaireService stagiaireService;
    private final DossierDocumentService dossierDocumentService;
    private final FormationService formationService;
    private final EvenementDocumentService evenementDocumentService;
    private final ValidationDocumentService validationDocumentService;

    @Autowired
    public AdminController(AdminService adminService, StagiaireService stagiaireService, DossierDocumentService dossierDocumentService, FormationService formationService, EvenementDocumentService evenementDocumentService, ValidationDocumentService validationDocumentService) {
        this.adminService = adminService;
        this.stagiaireService = stagiaireService;
        this.dossierDocumentService = dossierDocumentService;
        this.validationDocumentService = validationDocumentService;
        this.formationService = formationService;
        this.evenementDocumentService = evenementDocumentService;
    }


    // --------------------------------------------
    // ADMIN - Utilisateurs / Stagiaires
    // --------------------------------------------

    @IsAdmin
    @GetMapping("/admins")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAdminsByTypeAndNiveauDroit();
        return ResponseEntity.ok(admins);
    }

    /**
     * Récupère tous les stagiaires (vue restreinte).
     *
     * @return Liste de tous les stagiaires
     */
    @IsAdmin
    @GetMapping("/stagiaires")
    @JsonView(AffichageDossier.Stagiaire.class)
    public ResponseEntity<List<Stagiaire>> getAllStagiaires() {
        return ResponseEntity.ok(stagiaireService.findAll());
    }

    /**
     * Récupère tous les stagiaires (vue complète) SEULEMENT ADMINS.
     */
    @IsAdmin
    @GetMapping("/stagiaires/complet")
    @JsonView(AffichageDossier.Complet.class)
    public ResponseEntity<List<Stagiaire>> getAllStagiairesComplet() {
        return ResponseEntity.ok(stagiaireService.findAll());
    }

    /**
     * Récupère les stagiaires par leur statut d'inscription (ex. EN_ATTENTE, VALIDEE…)
     * (accessible à tous les admins uniquement).
     *
     * @param statut Statut d'inscription à filtrer
     * @return Stagiaire selon son statut
     */
    @IsAdmin
    @GetMapping("/stagiaires/par-statut")
    public ResponseEntity<List<Stagiaire>> getStagiairesByStatut(
            @RequestParam StatutInscription statut) {
        return ResponseEntity.ok(stagiaireService.findByStatutInscription(statut));
    }

    /**
     * Récupère les stagiaires dont la date d'inscription est comprise entre deux dates.
     * (accessible à tous les admins uniquement).
     *
     * @param debut Date de début (inclus)
     * @param fin   Date de fin (inclus)
     * @return
     */
    @IsAdmin
    @GetMapping("stagiaires/inscrits")
    public ResponseEntity<List<Stagiaire>> getStagiairesInscritsEntre(
            @RequestParam LocalDate debut,
            @RequestParam LocalDate fin) {
        return ResponseEntity.ok(stagiaireService.findInscritsEntre(debut, fin));
    }

    /**
     * Crée un nouveau stagiaire, avec ou sans inscription à une formation.
     *
     * @param stagiaire   Données du nouveau stagiaire
     * @param formationId ID de la formation (optionnel)
     * @return Stagiaire créé
     */
    @IsAdmin
    @PostMapping("/stagiaires")
    public ResponseEntity<Stagiaire> createStagiaire(
            @RequestBody @Valid Stagiaire stagiaire,
            @RequestParam(required = false) Integer formationId) {

        Stagiaire saved = stagiaireService.save(stagiaire);

        if (formationId != null) {
            Optional<Formation> formationOpt =
                    formationService.findById(formationId);
            if (formationOpt.isEmpty()) {
                return ResponseEntity.badRequest().build(); // formation inconnue
            }
            // Rattachement direct à la formation
            stagiaireService.inscrireStagiaire(saved, formationOpt.get());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Met à jour un stagiaire existant.
     * SEULS : l’ADMIN peut modifier n’importe quel stagiaire ;
     * un stagiaire « lambda » ne peut modifier que son propre profil.
     *
     * @param id               ID du stagiaire
     * @param updatedStagiaire Données à mettre à jour
     * @return Stagiaire mis à jour ou code 404
     */
    @IsAdmin
    @PutMapping("/stagiaire/{id}")
    public ResponseEntity<Stagiaire> updateStagiaire(
            @PathVariable int id,
            @RequestBody @Valid Stagiaire updatedStagiaire,
            Principal principal,
            Authentication authentication) {

        Integer idConnecte = stagiaireService.getIdFromPrincipal(principal);
        boolean estAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        if (!estAdmin && !idConnecte.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Si on arrive ici, l'utilisateur est autorisé à modifier ce profil
        Optional<Stagiaire> opt = stagiaireService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Stagiaire existing = opt.get();
        existing.setLastName(updatedStagiaire.getLastName());
        existing.setFirstName(updatedStagiaire.getFirstName());
        existing.setEmail(updatedStagiaire.getEmail());

        return ResponseEntity.ok(stagiaireService.save(existing));
    }

    /**
     * Supprime un stagiaire par son ID.
     * SEULS : l’ADMIN peut supprimer n’importe quel stagiaire ;
     * un stagiaire « lambda » ne peut supprimer que son propre compte.
     *
     * @param id ID du stagiaire
     * @return Code 204 si supprimé, 404 sinon
     */
    @IsAdmin
    @DeleteMapping("/stagiaire/{id}")
    public ResponseEntity<Void> deleteStagiaire(@PathVariable int id) {

        if (!stagiaireService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        stagiaireService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------
    // ADMIN — Documents à traiter
    // --------------------------------------------


    /**
     * Récupère la liste de tous les documents en attente de validation (accessible aux admins).
     */
    @IsAdmin
    @GetMapping("/documents/en-attente")
    public ResponseEntity<List<Document>> getPendingDocuments() {
        List<Document> pending = dossierDocumentService.getPendingDocuments();
        return ResponseEntity.ok(pending);
    }

    /**
     * Valide un document soumis (change son statut en VALIDÉ).
     * Après validation, on déclenche une vérification du dossier complet du stagiaire.
     *
     * @param documentId ID du document à valider
     */
    @IsAdmin
    @PatchMapping("/documents/{documentId}/valider")
    public ResponseEntity<Void> validerDocument(@PathVariable Integer documentId) {
        try {
            dossierDocumentService.validerDocument(documentId, "VALIDÉ", null);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Rejette un document soumis (change son statut en REJETÉ).
     * Après rejet, on déclenche une vérification du dossier complet du stagiaire (pour rester INCOMPLET).
     *
     * @param documentId ID du document à rejeter
     */
    @IsAdmin
    @PatchMapping("/documents/{documentId}/rejeter")
    public ResponseEntity<Void> rejeterDocument(@PathVariable Integer documentId) {
        try {
            dossierDocumentService.rejeterDocument(documentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

}
