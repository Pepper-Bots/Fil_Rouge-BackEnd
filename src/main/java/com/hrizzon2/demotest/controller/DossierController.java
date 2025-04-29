package com.hrizzon2.demotest.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.security.AppUserDetails;
import com.hrizzon2.demotest.security.IsAdmin;
import com.hrizzon2.demotest.security.IsStagiaire;
import com.hrizzon2.demotest.service.DossierService;
import com.hrizzon2.demotest.view.AffichageDossier;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/// / Afficher les dossiers (liste)
/// / Afficher un dossier (choisi dans la liste)
@CrossOrigin // Permet les requêtes Cross-Origin (utile pour le frontend séparé du backend)
@RestController // Indique que cette classe est un contrôleur REST
@RequestMapping("/api/dossiers") // TODO nécessaire ? qu'est ce ça implique ?
public class DossierController {

    private final DossierService dossierService;

    // Constructeur avec injection de dépendances via @Autowired
    @Autowired
    // TODO chatpgt pas de @Autowired ?
    public DossierController(DossierService dossierService) {
        this.dossierService = dossierService;
    }


    // Endpoint GET pour récupérer un produit par son id
    // Accessible uniquement aux clients grâce à l’annotation personnalisée @IsStagiaire
    @GetMapping("/dossier/{id}")
    @IsStagiaire
    @JsonView(AffichageDossier.Dossier.class)
    public ResponseEntity<List<Dossier>> getAll() {
        List<Dossier> dossiers = dossierService.getAll();
        return new ResponseEntity<>(dossiers, HttpStatus.OK);
    }

    // Endpoint GET pour récupérer tous les dossiers
    // Accessible aux stagiaires
    @GetMapping("/dossier/{id}")
    @IsStagiaire
    @JsonView(AffichageDossier.Dossier.class)
    public ResponseEntity<Dossier> getById(@PathVariable int id) {
        try {
            Dossier dossier = dossierService.getById(id);
            // Sinon, on retourne le produit avec un code 200 OK
            return new ResponseEntity<>(dossier, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            // Si aucun produit trouvé, on retourne un code 404.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint POST pour créer un nouveau dossier
    // Accessible uniquement aux admins
//    @PostMapping("/dossier")
//    @IsAdmin
//    @JsonView(AffichageDossier.Dossier.class)
//    public ResponseEntity<Dossier> save(
//            @RequestBody @Valid Dossier dossier,
//            @AuthenticationPrincipal AppUserDetails userDetails) { // Récupère l'utilisateur connecté
//
//        // On définit le vendeur (créateur) du produit en fonction de l'utilisateur connecté
//        dossier.setCreateur((Admin) userDetails.getUser());
//
//        // Si l'état du produit n'est pas défini, on le met par défaut à "neuf" (id = 1)
//        if (dossier.getStatutDossier() == null) {
//            StatutDossier EN_ATTENTE_DE_VALIDATION = new StatutDossier();
//            EN_ATTENTE_DE_VALIDATION.setId(1);
//            dossier.setStatutDossier(EN_ATTENTE_DE_VALIDATION);
//            // TODO remplacer par StatutDossier ?
//        }

    // On s'assure que l'ID est null pour forcer la création d’un nouveau produit
//        dossier.setId(null);
//
//        // On sauvegarde le produit en base
//        dossierDao.save(dossier);
//
//        // On retourne le produit avec un statut 201 CREATED
//        return new ResponseEntity<>(dossier, HttpStatus.CREATED);
//    }

    @PostMapping
    @IsAdmin
    @JsonView(AffichageDossier.Dossier.class)
    public ResponseEntity<Dossier> create(@Valid @RequestBody Dossier dossier,
                                          @AuthenticationPrincipal AppUserDetails userDetails) {
        Dossier created = dossierService.create(dossier, userDetails);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/dossier/{id}")
    @IsAdmin
    @JsonView(AffichageDossier.Dossier.class)
    public ResponseEntity<Dossier> update(@PathVariable int id,
                                          @RequestBody @Valid Dossier dossier,
                                          @AuthenticationPrincipal AppUserDetails userDetails) {
        try {
            Dossier updated = dossierService.update(id, dossier, userDetails);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SecurityException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // TODO Pourquoi <Void> ?
    // Endpoint DELETE pour supprimer un dossier
    // Accessible uniquement aux stagiaires
    @DeleteMapping("/dossier/{id}")
    @IsAdmin
    public ResponseEntity<Void> delete(
            @PathVariable int id,
            @AuthenticationPrincipal AppUserDetails userDetails) {

        try {
            dossierService.delete(id, userDetails);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SecurityException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
//        Optional<Dossier> optionalDossier = dossierDao.findById(id);
//
//        // Si le produit n'existe pas, on retourne une erreur 404.
//        if (optionalDossier.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        // Récupère le rôle de l'utilisateur connecté
//        String role = securityUtils.getRole(userDetails);
//
//        // Vérifie que l'utilisateur est soit "CHEF_RAYON", soit le créateur du produit
//        if (!role.equals("ROLE_ADMIN") &&
//                optionalDossier.get().getCreateur().getId() != userDetails.getUser().getId()) {
//            // L'utilisateur n'est pas autorisé à supprimer ce produit
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//
//        // Suppression du produit
//        dossierDao.deleteById(id);

    // Retourne un code 204 (No Content)
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
}


// Endpoint PUT pour mettre à jour un produit
// Accessible uniquement aux vendeurs
//    @PutMapping("/dossier/{id}")
//    @IsAdmin
//    public ResponseEntity<Dossier> update(
//            @PathVariable int id,
//            @RequestBody @Valid Dossier dossier,
//            @AuthenticationPrincipal AppUserDetails userDetails) {
//
//        Optional<Dossier> optionalDossier = dossierDao.findById(id);
//
//        // Si le produit à modifier n'existe pas, on retourne une erreur 404.
//        if (optionalDossier.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        // Récupère le rôle de l'utilisateur connecté
//        String role = securityUtils.getRole(userDetails);
//
//        // Vérifie que l'utilisateur est soit "ROLE_ADMIN", soit le créateur du dossier
//        if (!role.equals("ROLE_ADMIN") &&
//                optionalDossier.get().getCreateur().getId() != userDetails.getUser().getId()) {
//            // L'utilisateur n'est pas autorisé à modifier ce dossier
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//
//        // Préserve le créateur original
//        dossier.setCreateur(optionalDossier.get().getCreateur());
//
//        // On définit l'id du produit à mettre à jour
//        dossier.setId(id);
//
//        // Mise à jour du produit en base
//        dossierDao.save(dossier);
//
//        // Retourne le produit modifié avec un statut 204 (No Content)
//        return new ResponseEntity<>(dossier, HttpStatus.NO_CONTENT);
//    }


