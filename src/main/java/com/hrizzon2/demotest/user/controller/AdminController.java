package com.hrizzon2.demotest.user.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.formation.service.FormationService;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import com.hrizzon2.demotest.security.IsAdmin;
import com.hrizzon2.demotest.service.DossierDocumentService;
import com.hrizzon2.demotest.service.EvenementDocumentService;
import com.hrizzon2.demotest.service.ValidationDocumentService;
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

    // Exemples d’usage d’AdminService
    @IsAdmin
    @GetMapping("/admins")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAdminsByTypeAndNiveauDroit();
        return ResponseEntity.ok(admins);
    }

    // --------------------------------------------
    // 3. Endpoints « CRUD classique pour les stagiaires »
    // --------------------------------------------

    /**
     * Récupère tous les stagiaires (vue restreinte).
     *
     * @return Liste de tous les stagiaires
     */
    // S'assurer que ton endpoint /stagiaires dans StagiaireController est accessible
    // et retourne une liste d'objets Stagiaire au format JSON.
    @IsAdmin
    @GetMapping("/stagiaires")
    @JsonView(AffichageDossier.Stagiaire.class)
    public ResponseEntity<List<Stagiaire>> getAllStagiaires() {

        List<Stagiaire> liste = stagiaireService.findAll();
        return ResponseEntity.ok(liste);
    }

    /**
     * Récupère tous les stagiaires (vue complète) SEULEMENT ADMINS.
     */
    @IsAdmin
    @GetMapping("/complet")
    @JsonView(AffichageDossier.Complet.class)
    public ResponseEntity<List<Stagiaire>> getAllStagiairesComplet() {
        return ResponseEntity.ok(stagiaireService.findAll());
    }

    // todo -> méthode nécessaire ?

    /**
     * Récupère tous les stagiaires (vue admin) – SEULEMENT ADMIN. — Identique à getAllStagiaires(),
     * mais illustré ici pour montrer la différence de JsonView.
     */
    @GetMapping("/admin")
    @JsonView(AffichageDossier.Admin.class)
    public ResponseEntity<List<Stagiaire>> getAllStagiairesAdmin() {
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
    @GetMapping("/statut")
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
    @GetMapping("/inscrits")
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
    @PostMapping("/stagiaire")
    public ResponseEntity<Stagiaire> createStagiaire(
            @RequestBody @Valid Stagiaire stagiaire,
            @RequestParam(required = false) Integer formationId) {

        Stagiaire saved = stagiaireService.save(stagiaire);  // Sauvegarde via le service

        // Si formationId est fourni, on associe le stagiaire à cette formation
        if (formationId != null) {
            Optional<Formation> formationOpt =
                    formationService.findById(formationId);
            if (formationOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            // On inscrit le stagiaire à la formation dès la création
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

        // Vérifier l'autorisation : ADMIN ou bien identique à id connecté
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

        Stagiaire sauv = stagiaireService.save(existing);
        return ResponseEntity.ok(sauv);
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
    // 6. Endpoints « Admin
    // → consultation et validation des documents »
    // --------------------------------------------

    // todo -> adapter méthode en 2 méthodes distinctes

    /**
     * Récupère la liste de tous les documents en attente de validation (accessible aux admins).
     */
    @IsAdmin
    @GetMapping("/admin/documents/pending")
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
    @PatchMapping("/admin/documents/{documentId}/valider")
    public ResponseEntity<Void> validerDocument(@PathVariable Integer documentId) {
        try {
            dossierDocumentService.validerDocument(documentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Rejette un document soumis (change son statut en REJETÉ).
     * Après rejet, on déclenche une vérification du dossier complet du stagiaire (pour rester INCOMPLET).
     *
     * @param documentId ID du document à rejeter
     */
    @IsAdmin
    @PatchMapping("/admin/documents/{documentId}/rejeter")
    public ResponseEntity<Void> rejeterDocument(@PathVariable Integer documentId) {
        try {
            dossierDocumentService.rejeterDocument(documentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

}

// TODO -> créer des méthodes séparées
//  1) pour gérer les documents qui vont compléter le dossier (DossierDocumentService) pour valider ou rejeter un document (et qui vont compléter le dossier d'inscription)
//  2) pour gérer les documents qui peuvent être envoyés pour justifier un évènement (EvenementDocumentService) pour valider ou rejeter

// TODO -> est ce que toutes les méthodes get pour récupérer un stagiaire sont nécessaires ?

// Controller sans service :
//C’est possible mais déconseillé, surtout dans des applications à logique métier.
// Le controller ne doit pas contenir de logique métier, juste orchestrer les appels.
// Si tu n’as pas de logique métier, un controller peut appeler directement le DAO, mais ce n’est pas une bonne pratique
// (difficile à maintenir, tester, sécuriser).