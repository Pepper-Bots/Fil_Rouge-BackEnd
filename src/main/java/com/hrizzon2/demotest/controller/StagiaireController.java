package com.hrizzon2.demotest.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.annotation.ValidFile;
import com.hrizzon2.demotest.model.Document;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.service.DossierDocumentService;
import com.hrizzon2.demotest.service.EvenementDocumentService;
import com.hrizzon2.demotest.service.FichierService;
import com.hrizzon2.demotest.service.FormationService;
import com.hrizzon2.demotest.service.Stagiaire.StagiaireService;
import com.hrizzon2.demotest.view.AffichageDossier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

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
    private final DossierDocumentService dossierDocumentService;
    private final EvenementDocumentService evenementDocumentService;
    private final FormationService formationService;
    private final FichierService fichierService;

    @Autowired
    public StagiaireController(StagiaireService stagiaireService, DossierDocumentService dossierDocumentService, EvenementDocumentService evenementDocumentService, FormationService formationService, FichierService fichierService) {
        this.stagiaireService = stagiaireService;
        this.dossierDocumentService = dossierDocumentService;
        this.evenementDocumentService = evenementDocumentService;
        this.formationService = formationService;
        this.fichierService = fichierService;
    }

    // --------------------------------------------
    //  1. Gestion profil
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


//    // --- Upload document obligatoire lié au dossier ---
//    @PostMapping(value = "/me/documents", consumes = "multipart/form-data")
//    public ResponseEntity<Void> uploadDocument(Principal principal,
//                                               @RequestParam("fichier") MultipartFile fichier,
//                                               @RequestParam("type") String typeDocument) {
//        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);
//        try {
//            dossierDocumentService.uploadDocument(idStagiaire, fichier, TypeDocument.valueOf(typeDocument));
//            return ResponseEntity.status(HttpStatus.CREATED).build();
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.badRequest().build();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    // --- Upload justificatif lié à un évènement (absence, retard) ---
//    @PostMapping(value = "/me/evenements/{evenementId}/documents", consumes = "multipart/form-data")
//    public ResponseEntity<Void> uploadDocumentEvenement(Principal principal,
//                                                        @PathVariable Integer evenementId,
//                                                        @RequestParam("fichier") MultipartFile fichier,
//                                                        @RequestParam("type") String typeDocument) {
//        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);
//        try {
//            // Optionnel : vérifier que l'évènement appartient bien au stagiaire idStagiaire (sécurité)
//            evenementDocumentService.uploadDocument(evenementId, fichier, TypeDocument.valueOf(typeDocument));
//            return ResponseEntity.status(HttpStatus.CREATED).build();
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.badRequest().build();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * Récupère la liste des documents déjà soumis par le stagiaire connecté.
     * Chaque document contient son status (EN_ATTENTE, VALIDÉ, REJETÉ).
     */
    @GetMapping("/me/documents")
    public ResponseEntity<List<Document>> getMesDocuments(Principal principal) {
        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);
        List<Document> liste = dossierDocumentService.getDocumentsByStagiaire(idStagiaire);
        return ResponseEntity.ok(liste);
    }

    /**
     * Récupère le dossier (liste complète des documents + statut global) du stagiaire connecté.
     */
    @GetMapping("/me/dossier")
    public ResponseEntity<Dossier> getMonDossier(Principal principal) {
        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);
        Dossier dossier = dossierDocumentService.getDossierCompletPourStagiaire(idStagiaire);
        if (dossier == null) {
            // Si aucun dossier n’existe (aucun document n’a encore été uploadé)
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dossier);
    }


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

    // --- Lister tous les justificatifs d’évènements ---
//    @GetMapping("/me/evenements/{evenementId}/documents")
//    public ResponseEntity<List<Document>> getDocumentsEvenement(Principal principal,
//                                                                @PathVariable Integer evenementId) {
//        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);
//        // Optionnel : vérifier que l'évènement appartient bien au stagiaire idStagiaire (sécurité)
//        List<Document> liste = evenementDocumentService.getDocumentsByEvenement(evenementId);
//        return ResponseEntity.ok(liste);
//    }

//    // --- Supprimer un document (dossier ou évènement) ---
//    @DeleteMapping("/me/documents/{documentId}")
//    public ResponseEntity<Void> deleteDocument(Principal principal,
//                                               @PathVariable Integer documentId) {
//        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);
//        // Optionnel : vérifier que le document appartient bien au stagiaire idStagiaire
//        boolean deleted = dossierDocumentService.supprimerDocument(documentId);
//        if (!deleted) {
//            deleted = evenementDocumentService.supprimerDocument(documentId);
//        }
//        if (deleted) {
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.notFound().build();
//    }


    // getMonProfil(Principal)
    //
    //mettreAJourMonProfil(Principal, Stagiaire)
    //
    //uploadPhotoProfil(id, MultipartFile)


    // --------------------------------------------
    //  2. Consultation documents personnels
    // --------------------------------------------

    // getMesDocuments(Principal)
    //
    //getMonDossier(Principal)

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


    // --------------------------------------------
    // 5. Endpoints « documents / dossier »
    // (inchangés, tels que définis précédemment)
    // --------------------------------------------

//    /**
//     * Permet au stagiaire connecté d'uploader un document obligatoire.
//     * <ul>
//     *   <li>Le paramètre `type` correspond au type de document (enum TypeDocument).</li>
//     *   <li>Le fichier est reçu en tant que MultipartFile.</li>
//     * </ul>
//     *
//     * @param principal Principal du stagiaire connecté
//     * @param type      Type de document (ex. CV, AttestationStage, PhotocopieCIN, ...)
//     * @param fichier   Le fichier envoyé
//     */
//    @PostMapping(value = "/me/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Void> uploadDocument(
//            Principal principal,
//            @RequestParam("fichier") MultipartFile fichier,
//            @RequestParam("type") TypeDocument type
//    ) {
//
//        // 1. On récupère l’ID du stagiaire actuel
//        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);
//
//        // 2. On délègue au service DocumentService pour gérer la persistance
//        try {
//            documentService.uploadDocument(idStagiaire, fichier, type);
//        } catch (IllegalArgumentException ex) {
//            // Par exemple : type de document déjà uploadé, format non autorisé, etc.
//            return ResponseEntity.badRequest().build();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }


// === EXEMPLE pour upload d’une image de profil stagiaire ===
// À adapter à ton modèle et à placer plutôt dans un StagiaireController !


}

// TODO - getProfil, updateProfil, getMesDocuments (consultation)
//  Conserver
//  Ne pas gérer upload ici, uniquement consultation