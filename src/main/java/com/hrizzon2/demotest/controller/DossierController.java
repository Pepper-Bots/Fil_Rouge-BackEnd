package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.dao.DossierDao;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.model.EtatDossier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
public class DossierController {

    protected DossierDao dossierDao;

    @Autowired
    public DossierController(DossierDao dossierDao) {
        this.dossierDao = dossierDao;
    }

    @GetMapping("/dossier/{id}")
    public ResponseEntity<Dossier> get(@PathVariable int id) {

        Optional<Dossier> optionalDossier = dossierDao.findById(id);

        if (optionalDossier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalDossier.get(), HttpStatus.OK);
    }

    @GetMapping("/dossiers")
    public List<Dossier> getAll() {

        return dossierDao.findAll();
    }

    @PostMapping("/dossier")
    public ResponseEntity<Dossier> save(@RequestBody @Valid Dossier dossier) {

        // Si le dossier reçu n'a pas d'état alors on indique qu'il est nouveau par défaut
        if (dossier.getEtatDossier() == null) {

            EtatDossier etatDossierNouveau = new EtatDossier();
            etatDossierNouveau.setId(1);
            dossier.setEtatDossier(etatDossierNouveau);
        }

        dossier.setId(null);
        dossierDao.save(dossier);
        return new ResponseEntity<>(dossier, HttpStatus.CREATED);
    }

    @DeleteMapping("/dossier/{id}")
    public ResponseEntity<Dossier> delete(@PathVariable int id) {

        Optional<Dossier> optionalDossier = dossierDao.findById(id);

        if (optionalDossier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        dossierDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/dossier/{id}")
    public ResponseEntity<Dossier> update(
            @PathVariable int id,
            @RequestBody @Valid Dossier dossier) {

        Optional<Dossier> optionalDossier = dossierDao.findById(id);

        if (optionalDossier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        dossier.setId(id);

        dossierDao.save(dossier);

        return new ResponseEntity<>(dossier, HttpStatus.NO_CONTENT);
    }
}

// Afficher les dossiers (liste)
// Afficher un dossier (choisi dans la liste)
