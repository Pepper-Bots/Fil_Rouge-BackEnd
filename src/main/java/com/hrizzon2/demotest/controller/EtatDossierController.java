package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.dao.EtatDossierDao;
import com.hrizzon2.demotest.model.StatutDossier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
public class EtatDossierController {

    protected EtatDossierDao etatDossierDao;

    @Autowired
    public EtatDossierController(EtatDossierDao etatDossierDao) {
        this.etatDossierDao = etatDossierDao;
    }

    @GetMapping("/etatdossier/{id}")
    public ResponseEntity<StatutDossier> getEtatDossier(@PathVariable int id) {

        Optional<StatutDossier> optionalEtatDossier = etatDossierDao.findById(id);

        if (optionalEtatDossier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalEtatDossier.get(), HttpStatus.OK);
    }

    @GetMapping("/etatdossiers")
    public List<StatutDossier> getAll() {

        return etatDossierDao.findAll();
    }

    @PostMapping("/etatdossier")
    public ResponseEntity<StatutDossier> save(@RequestBody StatutDossier statutDossier) {
        etatDossierDao.save(statutDossier);

        return new ResponseEntity<>(statutDossier, HttpStatus.CREATED);
    }

    @DeleteMapping("/etatdossier/{id}")
    public ResponseEntity<StatutDossier> delete(@PathVariable int id) {

        Optional<StatutDossier> optionalEtatDossier = etatDossierDao.findById(id);

        if (optionalEtatDossier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        etatDossierDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/etatdossier/{id}")
    public ResponseEntity<StatutDossier> update(
            @PathVariable int id,
            @RequestBody StatutDossier statutDossier) {

        Optional<StatutDossier> optionalEtatDossier = etatDossierDao.findById(id);

        if (optionalEtatDossier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        statutDossier.setId(id);

        etatDossierDao.save(statutDossier);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

