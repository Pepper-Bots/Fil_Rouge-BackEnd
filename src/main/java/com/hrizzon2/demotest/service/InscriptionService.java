package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.dao.InscriptionDao;
import com.hrizzon2.demotest.model.Inscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InscriptionService {

    // Service pour gérer les inscriptions et leurs règles métier (par exemple, validation, expiration, etc.).

    private final InscriptionDao inscriptionDao;

    @Autowired
    public InscriptionService(InscriptionDao inscriptionDao) {
        this.inscriptionDao = inscriptionDao;
    }

    public List<Inscription> getAllInscriptions() {
        return inscriptionDao.findAll();
    }

    public Optional<Inscription> getInscriptionById(Integer id) {
        return inscriptionDao.findById(id);
    }

    public Inscription saveInscription(Inscription inscription) {
        return inscriptionDao.save(inscription);
    }

    public void deleteInscription(Integer id) {
        inscriptionDao.deleteById(id);
    }
}
