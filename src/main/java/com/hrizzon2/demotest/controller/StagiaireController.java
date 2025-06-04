package com.hrizzon2.demotest.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import com.hrizzon2.demotest.security.IsAdmin;
import com.hrizzon2.demotest.service.DocumentService;
import com.hrizzon2.demotest.service.FormationService;
import com.hrizzon2.demotest.service.stagiaire.StagiaireService;
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

// le controller s'occupe exclusivement de la réception des requêtes HTTP

/**
 * Contrôleur REST pour la gestion des stagiaires, de leur profil,
 * et du dépôt/validation des documents obligatoires.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/stagiaires")
public class StagiaireController {

    protected StagiaireService stagiaireService;
    protected FormationService formationService;
    protected DocumentService documentService;

    @Autowired
    public StagiaireController(StagiaireService stagiaireService, FormationService formationService, DocumentService documentService) {
        this.stagiaireService = stagiaireService;
        this.formationService = formationService;
        this.documentService = documentService;
    }


    // ----------------------------
    // 1. Endpoints réservés aux ADMIN
    // ----------------------------

    /**
     * Récupère tous les stagiaires.
     *
     * @return Liste de tous les stagiaires
     */
    // S'assurer que ton endpoint /stagiaires dans StagiaireController est accessible
    // et retourne une liste d'objets Stagiaire au format JSON.
    @IsAdmin
    @GetMapping
    @JsonView(AffichageDossier.Stagiaire.class)
    public ResponseEntity<List<Stagiaire>> getAllStagiaires() {
        return ResponseEntity.ok(stagiaireService.findAll());
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

    /**
     * Récupère tous les stagiaires (vue admin) – identique à getAllStagiaires(),
     * mais illustré ici pour montrer la différence de JsonView.
     */
    @GetMapping("/admin")
    @JsonView(AffichageDossier.Admin.class)
    public ResponseEntity<List<Stagiaire>> getAllStagiairesAdmin() {
        return ResponseEntity.ok(stagiaireService.findAll());
    }


    // --------------------------------------------
    // 2. Endpoints « sécurisés » qui peuvent être consultés
    //    mais en limitant aux propres données du stagiaire connecté
    // --------------------------------------------


    /**
     * Récupère un stagiaire par son identifiant.
     * <ul>
     *   <li>Si l'utilisateur est ADMIN → peut accéder à n'importe quel id.</li>
     *   <li>Sinon (stagiaire "normal") → ne peut consulter que son propre profil (id=le sien).</li>
     * </ul>
     *
     * @param id        Identifiant du stagiaire demandé
     * @param principal Principal Spring Security (dont getName() est l'email)
     */
    @GetMapping("/{id}")
    @JsonView(AffichageDossier.Stagiaire.class)
    public ResponseEntity<Stagiaire> getStagiaireById(
            @PathVariable int id,
            Principal principal,
            Authentication authentication) {

        // 1. On récupère l'ID du stagiaire connecté depuis le Principal
        Integer idConnecte = stagiaireService.getIdFromPrincipal(principal);

        // 2. On vérifie si l'utilisateur a bien le rôle ADMIN
        boolean estAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        // 3. Si ce n'est ni le profil demandé, ni un admin, on renvoie 403
        if (!estAdmin && !idConnecte.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 4. Sinon on retourne le stagiaire s'il existe
        return stagiaireService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    // ----------------------------
    // 3. Les autres endpoints « publics » pour l'inscription, la mise à jour, la suppression
    //    (suivant que vous souhaitiez restreindre certains à ADMIN ou pas)
    // ----------------------------


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
     * @param stagiaire   Données du stagiaire
     * @param formationId ID de la formation (optionnel)
     * @return Stagiaire créé
     */
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
    @DeleteMapping("/stagiaire/{id}")
    public ResponseEntity<Void> deleteStagiaire(
            @PathVariable int id,
            Principal principal,
            Authentication authentication) {

        Integer idConnecte = stagiaireService.getIdFromPrincipal(principal);
        boolean estAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        if (!estAdmin && !idConnecte.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!stagiaireService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        stagiaireService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    // --------------------------------------------
    // 2. Endpoints « mon profil connecté »
    // --------------------------------------------

    /**
     * Retourne le profil complet du stagiaire connecté.
     * On utilise getIdFromPrincipal pour récupérer l’ID en base.
     * Spring injecte automatiquement le Principal (contenant l’username/email).
     *
     * @param principal Principal fourni par Spring Security
     */
    @GetMapping("/me")
    @JsonView(AffichageDossier.Stagiaire.class)
    public ResponseEntity<Stagiaire> getMonProfil(Authentication authentication) {

        // Récupère le login/email depuis le token JWT ou le principal
        String email = authentication.getName(); // ou .getPrincipal().getUsername() selon config
        return stagiaireService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
