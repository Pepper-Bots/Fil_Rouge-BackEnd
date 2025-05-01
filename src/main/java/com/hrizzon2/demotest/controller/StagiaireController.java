package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.dao.StagiaireDao;
import com.hrizzon2.demotest.model.Inscription;
import com.hrizzon2.demotest.model.Stagiaire;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class StagiaireController {

    protected final StagiaireDao stagiaireDao;
    @Setter
    @Getter
    private Inscription inscription;

    @Autowired
    public StagiaireController(StagiaireDao stagiaireDao, Inscription inscription) {
        this.stagiaireDao = stagiaireDao;
        this.inscription = inscription;
    }

    @GetMapping("/stagiaire/{id}")
    public ResponseEntity<Stagiaire> get(@PathVariable int id) {
        Optional<Stagiaire> optionalStagiaire = stagiaireDao.findById(id);

        if (optionalStagiaire.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalStagiaire.get(), HttpStatus.OK);
    }

    @GetMapping("/stagiaires")
    public List<Stagiaire> getAll() {
        return (List<Stagiaire>) stagiaireDao.findAll();
    }

    @PostMapping("/stagiaire")
    public ResponseEntity<Stagiaire> save(@RequestBody @Valid Stagiaire stagiaire) {
        if (stagiaire.getInscription() == null) {
            Inscription inscriptionNeuf = new Inscription();
            inscriptionNeuf.setId(1); // TODO attention : à remplacer plus tard par une logique réelle
            stagiaire.setInscription(inscriptionNeuf);
        }

        stagiaire.setId(null);  // pour forcer une création
        stagiaireDao.save(stagiaire);
        return new ResponseEntity<>(stagiaire, HttpStatus.CREATED);
    }

// Exemple de méthode fictive utilisée pour tester (désactivée)

    // public Stagiaire get() {
    //     Stagiaire stagiaire = new Stagiaire();
    //     stagiaire.setNom("Dupont");
    //     stagiaire.setPrenom("John");
    //     return stagiaire;
    // }

    @PutMapping("/stagiaire/{id}")
    public ResponseEntity<Stagiaire> update(@PathVariable int id, @RequestBody @Valid Stagiaire stagiaire) {
        Optional<Stagiaire> optionalStagiaire = stagiaireDao.findById(id);
        if (optionalStagiaire.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Stagiaire existing = optionalStagiaire.get();
        existing.setNom(stagiaire.getNom());
        existing.setPrenom(stagiaire.getPrenom());
        existing.setInscription(stagiaire.getInscription()); // ou juste son ID

        stagiaireDao.save(existing);
        return new ResponseEntity<>(existing, HttpStatus.OK);
    }

    @DeleteMapping("/stagiaire/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (!stagiaireDao.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        stagiaireDao.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
