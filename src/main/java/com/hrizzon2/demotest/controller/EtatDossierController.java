package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.dao.EtatDossierDao;
import com.hrizzon2.demotest.model.EtatDossier;
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
    public ResponseEntity<EtatDossier> getEtatDossier(@PathVariable int id) {

        Optional<EtatDossier> optionalEtatDossier = etatDossierDao.findById(id);

        if (optionalEtatDossier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalEtatDossier.get(), HttpStatus.OK);
    }

    @GetMapping("/etatdossiers")
    public List<EtatDossier> getAll() {

        return etatDossierDao.findAll();
    }

    @PostMapping("/etatdossier")
    public ResponseEntity<EtatDossier> save(@RequestBody EtatDossier etatDossier) {
        etatDossierDao.save(etatDossier);

        return new ResponseEntity<>(etatDossier, HttpStatus.CREATED);
    }

    @DeleteMapping("/etatdossier/{id}")
    public ResponseEntity<EtatDossier> delete(@PathVariable int id) {

        Optional<EtatDossier> optionalEtatDossier = etatDossierDao.findById(id);

        if (optionalEtatDossier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        etatDossierDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/etatdossier/{id}")
    public ResponseEntity<EtatDossier> update(
            @PathVariable int id,
            @RequestBody EtatDossier etatDossier) {

        Optional<EtatDossier> optionalEtatDossier = etatDossierDao.findById(id);

        if (optionalEtatDossier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        etatDossier.setId(id);

        etatDossierDao.save(etatDossier);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

