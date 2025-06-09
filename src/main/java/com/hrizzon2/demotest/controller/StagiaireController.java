package com.hrizzon2.demotest.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.annotation.ValidFile;
import com.hrizzon2.demotest.model.Document;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import com.hrizzon2.demotest.security.IsAdmin;
import com.hrizzon2.demotest.service.DocumentService;
import com.hrizzon2.demotest.service.FichierService;
import com.hrizzon2.demotest.service.FormationService;
import com.hrizzon2.demotest.service.stagiaire.StagiaireService;
import com.hrizzon2.demotest.view.AffichageDossier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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

    private final StagiaireService stagiaireService;
    private final FormationService formationService;
    private final DocumentService documentService;
    private final FichierService fichierService;

    @Autowired
    public StagiaireController(StagiaireService stagiaireService, FormationService formationService, DocumentService documentService, FichierService fichierService) {
        this.stagiaireService = stagiaireService;
        this.formationService = formationService;
        this.documentService = documentService;
        this.fichierService = fichierService;
    }


    // ----------------------------
    // 1. Endpoints réservés aux ADMIN
    // ----------------------------

    /**
     * Récupère tous les stagiaires (vue restreinte).
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
     * Récupère tous les stagiaires (vue admin) – SEULEMENT ADMIN. — Identique à getAllStagiaires(),
     * mais illustré ici pour montrer la différence de JsonView.
     */
    @GetMapping("/admin")
    @JsonView(AffichageDossier.Admin.class)
    public ResponseEntity<List<Stagiaire>> getAllStagiairesAdmin() {
        return ResponseEntity.ok(stagiaireService.findAll());
    }


    // --------------------------------------------
    //  2. Endpoints « sécurisés » – accès au profil par ID
    // --------------------------------------------


    /**
     * Récupère un stagiaire par son identifiant.
     * <ul>
     *   <li>Si l'utilisateur est ADMIN → peut accéder à n'importe quel id.</li>
     *   <li>Sinon (stagiaire "normal") → ne peut consulter QUE son propre profil (id=le sien).</li>
     * </ul>
     *
     * @param id             Identifiant du stagiaire demandé
     * @param principal      Principal Spring Security (donne l'email)
     * @param authentication Authentication, pour vérifier ROLE_ADMIN
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

        // 3. Si ce n'est ni le profil demandé, ni un admin, on interdit l'accès, on renvoie 403
        if (!estAdmin && !idConnecte.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 4. Sinon on charge et on retourne le stagiaire s'il existe
        return stagiaireService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    // ----------------------------
    // 3. Endpoints « CRUD » – création et mise à jour
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

    // --------------------------------------------
    // 3. Endpoints « CRUD classique pour les stagiaires »
    // --------------------------------------------

    /**
     * Crée un nouveau stagiaire, avec ou sans inscription à une formation.
     *
     * @param stagiaire   Données du nouveau stagiaire
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
    // 4. Endpoints « mon profil connecté »
    // --------------------------------------------

    /**
     * Retourne le profil complet du stagiaire connecté.
     * <p>
     * On utilise getIdFromPrincipal pour récupérer l’ID en base.
     * Spring injecte automatiquement le Principal (contenant l’username/email).
     *
     * @param principal Principal fourni par Spring Security
     */
    @GetMapping("/me")
    @JsonView(AffichageDossier.Stagiaire.class)
    public ResponseEntity<Stagiaire> getMonProfil(Principal principal) {

        // 1. On récupère l’ID du stagiaire courant à partir du principal
        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);

        // 2. On charge l’entité en base (si introuvable, 404)
        Stagiaire stagiaire = stagiaireService.findById(idStagiaire)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Stagiaire non trouvé"));

        return ResponseEntity.ok(stagiaire);
    }


    /**
     * Met à jour uniquement le profil du stagiaire connecté (nom, prénom, email uniquement).
     * <br><em>Le stagiaire ne peut pas modifier d’autres champs sensibles,
     * et seul son propre compte est affecté.</em>
     *
     * @param principal        Principal courant
     * @param updatedStagiaire Contient les nouvelles valeurs à appliquer
     */
    @PutMapping("/me")
    public ResponseEntity<Stagiaire> mettreAJourMonProfil(
            Principal principal,
            @RequestBody @Valid Stagiaire updatedStagiaire) {

        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);
        Stagiaire existant = stagiaireService.findById(idStagiaire)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Stagiaire non trouvé"));

        existant.setFirstName(updatedStagiaire.getFirstName());
        existant.setLastName(updatedStagiaire.getLastName());
        existant.setEmail(updatedStagiaire.getEmail());

        Stagiaire sauv = stagiaireService.save(existant);
        return ResponseEntity.ok(sauv);
    }

    // --------------------------------------------
    // 5. Endpoints « documents / dossier »
    // (inchangés, tels que définis précédemment)
    // --------------------------------------------

    /**
     * Permet au stagiaire connecté d'uploader un document obligatoire.
     * <ul>
     *   <li>Le paramètre `type` correspond au type de document (enum TypeDocument).</li>
     *   <li>Le fichier est reçu en tant que MultipartFile.</li>
     * </ul>
     *
     * @param principal Principal du stagiaire connecté
     * @param type      Type de document (ex. CV, AttestationStage, PhotocopieCIN, ...)
     * @param fichier   Le fichier envoyé
     */
    @PostMapping(value = "/me/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadDocument(
            Principal principal,
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam("type") TypeDocument type
    ) {

        // 1. On récupère l’ID du stagiaire actuel
        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);

        // 2. On délègue au service DocumentService pour gérer la persistance
        try {
            documentService.uploadDocument(idStagiaire, fichier, type);
        } catch (IllegalArgumentException ex) {
            // Par exemple : type de document déjà uploadé, format non autorisé, etc.
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Récupère la liste des documents déjà soumis par le stagiaire connecté.
     * Chaque document contient son status (EN_ATTENTE, VALIDÉ, REJETÉ).
     */
    @GetMapping("/me/documents")
    public ResponseEntity<List<Document>> getMesDocuments(Principal principal) {
        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);
        List<Document> liste = documentService.getDocumentsByStagiaire(idStagiaire);
        return ResponseEntity.ok(liste);
    }

    /**
     * Récupère le dossier (liste complète des documents + statut global) du stagiaire connecté.
     */
    @GetMapping("/me/dossier")
    public ResponseEntity<Dossier> getMonDossier(Principal principal) {
        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);
        Dossier dossier = documentService.getDossierCompletPourStagiaire(idStagiaire);
        if (dossier == null) {
            // Si aucun dossier n’existe (aucun document n’a encore été uploadé)
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dossier);
    }

    // --------------------------------------------
    // 6. Endpoints « Admin
    // → consultation et validation des documents »
    // --------------------------------------------

    /**
     * Récupère la liste de tous les documents en attente de validation (accessible aux admins).
     */
    @IsAdmin
    @GetMapping("/admin/documents/pending")
    public ResponseEntity<List<Document>> getPendingDocuments() {
        List<Document> pending = documentService.getPendingDocuments();
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
            documentService.validerDocument(documentId);
        } catch (IllegalArgumentException ex) {
            // Par ex. document introuvable ou déjà validé/rejeté
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
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
            documentService.rejeterDocument(documentId);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

// === EXEMPLE pour upload d’une image de profil stagiaire ===
// À adapter à ton modèle et à placer plutôt dans un StagiaireController !


    @PostMapping("/stagiaire/{id}/photo")
    public ResponseEntity<?> uploadPhotoProfil(
            @PathVariable Integer id,
            @ValidFile @RequestParam("file") MultipartFile file) {
        try {
            // Stocke l’image, mets à jour le champ photo du stagiaire
            String nomImage = fichierService.sanitizeFileName(file.getOriginalFilename());
            fichierService.uploadToLocalFileSystem(file, nomImage, false);
            stagiaireService.updatePhotoProfil(id, nomImage);
            return ResponseEntity.ok("Photo envoyée !");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur d'upload");
        }
    }
}

