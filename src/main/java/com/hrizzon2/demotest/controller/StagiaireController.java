//package com.hrizzon2.demotest.controller;
//
//import com.hrizzon2.demotest.dao.StagiaireDao;
//import com.hrizzon2.demotest.model.Inscription;
//import com.hrizzon2.demotest.model.Stagiaire;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.awt.*;
//import java.util.Optional;
//
//@CrossOrigin
//@RestController
//public class StagiaireController {
//
//    protected StagiaireDao stagiaireDao;
//
//    @Autowired
//    public StagiaireController(StagiaireDao stagiaireDao) {
//        this.stagiaireDao = stagiaireDao;
//    }
//
//    @GetMapping("/stagiaire/{id}")
//    public ResponseEntity<Stagiaire> get(@PathVariable int id) {
//
//        Optional<Stagiaire> optionalStagiaire = stagiaireDao.findById(id);
//
//        if (optionalStagiaire.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(optionalStagiaire.get(), HttpStatus.OK);
//    }
//
//    @GetMapping("/stagiaires")
//    public List<Stagiaire> getAll() {
//
//        return (List<Stagiaire>) stagiaireDao.findAll();
//    }
//
//    @PostMapping("/stagiaire")
//    public ResponseEntity<Stagiaire> save(@RequestBody @Valid Stagiaire stagiaire) {
//
//        if (stagiaire.getInscription() == null) {
//
//            Inscription inscriptionNeuf = new Inscription();
//            inscriptionNeuf.setId(1);
//            stagiaire.setInscription(inscriptionNeuf);
//        }
//
//        stagiaire.setId(null);
//        stagiaireDao.save(stagiaire);
//        return new ResponseEntity<>(stagiaire, HttpStatus.CREATED);
//    }
//
//
/// /    public Stagiaire get() {
/// /
/// /        Stagiaire stagiaire = new Stagiaire();
/// /
/// /        stagiaire.setNom("Dupont");
/// /        stagiaire.setPrenom("John");
/// /
/// /
/// /        return stagiaire;
/// /    }
//}
