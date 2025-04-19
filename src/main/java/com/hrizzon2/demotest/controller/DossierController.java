package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.dao.DossierDao;
import com.hrizzon2.demotest.model.Dossier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//@CrossOrigin
//@RestController
//public class DossierController {
//
//    protected DossierDao dossierDao;
//    protected StagiaireDao stagiaireDao;
//
//    @Autowired
//    public DossierController(DossierDao dossierDao, StagiaireDao stagiaireDao) {
//        this.dossierDao = dossierDao;
//        this.stagiaireDao = stagiaireDao;
//    }
//
//    @GetMapping("/dossier/{id}")
//    public ResponseEntity<Dossier> get(@PathVariable long id) {
//
//        Optional<Dossier> optionalDossier = dossierDao.findById(id);
//
//        if (optionalDossier.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(optionalDossier.get(), HttpStatus.OK);
//    }
//
//    @GetMapping("/dossiers")
//    public List<Dossier> getAllDossiers() {
//
//        return dossierDao.findAll();
//    }
//
//    @PostMapping("/dossier")
//    public ResponseEntity<Dossier> save(@RequestBody @Valid Dossier dossier) {
//
//        // Si le dossier reçu n'a pas d'état alors on indique qu'il est nouveau par défaut
//        if (dossier.getStatut() == null) {
//
//            StatutDossier NouveauDossier = new NouveauDossier();
//            NouveauDossier.setId(1);
//            dossier.setStatut(NouveauDossier);
//        }
//
//        dossier.setId(null);
//        dossierDao.save(dossier);
//        return new ResponseEntity<>(dossier, HttpStatus.CREATED);
//    }
//
//    @DeleteMapping("/dossier/{id}")
//    public ResponseEntity<Dossier> delete(@PathVariable int id) {
//
//        Optional<Dossier> optionalDossier = dossierDao.findById(id);
//
//        if (optionalDossier.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        dossierDao.deleteById(id);
//
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @PutMapping("/dossier/{id}")
//    public ResponseEntity<Dossier> update(
//            @PathVariable int id,
//            @RequestBody @Valid Dossier dossier) {
//
//        Optional<Dossier> optionalDossier = dossierDao.findById(id);
//
//        if (optionalDossier.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        dossier.setId(id);
//
//        dossierDao.save(dossier);
//
//        return new ResponseEntity<>(dossier, HttpStatus.NO_CONTENT);
//    }
//}
//

/// / Afficher les dossiers (liste)
/// / Afficher un dossier (choisi dans la liste)

//package com.hrizzon.demo2.controller;
//
//import com.hrizzon.demo2.dao.DossierDao;
//import com.hrizzon.demo2.model.Etat;
//import com.hrizzon.demo2.model.Dossier;
//import com.hrizzon.demo2.security.ISecuriteUtils;
//import com.hrizzon.demo2.model.Vendeur;
//import com.hrizzon.demo2.security.AppUserDetails;
//import com.hrizzon.demo2.security.IsClient;
//import com.hrizzon.demo2.security.IsVendeur;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;

@CrossOrigin // Permet les requêtes Cross-Origin (utile pour le frontend séparé du backend)
@RestController // Indique que cette classe est un contrôleur REST
public class DossierController {

    // Dépendances injectées : DAO pour gérer les produits et un utilitaire de sécurité
    protected DossierDao dossierDao;
    protected ISecuriteUtils securiteUtils;

    // Constructeur avec injection de dépendances via @Autowired
    @Autowired
    public DossierController(DossierDao dossierDao, ISecuriteUtils securiteUtils) {
        this.dossierDao = dossierDao;
        this.securiteUtils = securiteUtils;
    }

    // Endpoint GET pour récupérer un produit par son id
    // Accessible uniquement aux clients grâce à l’annotation personnalisée @IsClient
    @GetMapping("/dossier/{id}")
    @IsClient
    public ResponseEntity<Dossier> get(@PathVariable int id) {
        Optional<Dossier> optionalDossier = dossierDao.findById(id);

        // Si aucun produit trouvé, on retourne un code 404
        if (optionalDossier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Sinon, on retourne le produit avec un code 200 OK
        return new ResponseEntity<>(optionalDossier.get(), HttpStatus.OK);
    }

    // Endpoint GET pour récupérer tous les produits
    // Accessible aux clients
    @GetMapping("/dossiers")
    @IsClient
    public List<Dossier> getAll() {
        return dossierDao.findAll();
    }

    // Endpoint POST pour créer un nouveau produit
    // Accessible uniquement aux vendeurs
    @PostMapping("/dossier")
    @IsVendeur
    public ResponseEntity<Dossier> save(
            @RequestBody @Valid Dossier dossier,
            @AuthenticationPrincipal AppUserDetails userDetails) { // Récupère l'utilisateur connecté

        // On définit le vendeur (créateur) du produit en fonction de l'utilisateur connecté
        dossier.setCreateur((Vendeur) userDetails.getUtilisateur());

        // Si l'état du produit n'est pas défini, on le met par défaut à "neuf" (id = 1)
        if (dossier.getEtat() == null) {
            Etat etatNeuf = new Etat();
            etatNeuf.setId(1);
            dossier.setEtat(etatNeuf);
        }

        // On s'assure que l'ID est null pour forcer la création d’un nouveau produit
        dossier.setId(null);

        // On sauvegarde le produit en base
        dossierDao.save(dossier);

        // On retourne le produit avec un statut 201 CREATED
        return new ResponseEntity<>(dossier, HttpStatus.CREATED);
    }

    // Endpoint DELETE pour supprimer un produit
    // Accessible uniquement aux vendeurs
    @DeleteMapping("/dossier/{id}")
    @IsVendeur
    public ResponseEntity<Dossier> delete(
            @PathVariable int id,
            @AuthenticationPrincipal AppUserDetails userDetails) {

        Optional<Dossier> optionalDossier = dossierDao.findById(id);

        // Si le produit n'existe pas, on retourne une erreur 404
        if (optionalDossier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Récupère le rôle de l'utilisateur connecté
        String role = securiteUtils.getRole(userDetails);

        // Vérifie que l'utilisateur est soit "CHEF_RAYON", soit le créateur du produit
        if (!role.equals("ROLE_CHEF_RAYON") &&
                optionalDossier.get().getCreateur().getId() != userDetails.getUtilisateur().getId()) {
            // L'utilisateur n'est pas autorisé à supprimer ce produit
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Suppression du produit
        dossierDao.deleteById(id);

        // Retourne un code 204 (No Content)
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Endpoint PUT pour mettre à jour un produit
    // Accessible uniquement aux vendeurs
    @PutMapping("/dossier/{id}")
    @IsVendeur
    public ResponseEntity<Dossier> update(
            @PathVariable int id,
            @RequestBody @Valid Dossier dossier) {

        Optional<Dossier> optionalDossier = dossierDao.findById(id);

        // Si le produit à modifier n'existe pas, on retourne une erreur 404
        if (optionalDossier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // On définit l'id du produit à mettre à jour
        dossier.setId(id);

        // Mise à jour du produit en base
        dossierDao.save(dossier);

        // Retourne le produit modifié avec un statut 204 (No Content)
        return new ResponseEntity<>(dossier, HttpStatus.NO_CONTENT);
    }
}

