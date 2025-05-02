//Service pour gérer les formations et vérifier la disponibilité, les horaires, etc.
package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.dao.FormationDao;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.Stagiaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FormationService {

    private final FormationDao formationDao;

    @Autowired
    public FormationService(FormationDao formationDao) {
        this.formationDao = formationDao;
    }

    public List<Formation> findAll() {
        return formationDao.findAll();
    }

    public Optional<Formation> findById(Integer id) {
        return formationDao.findById(id);
    }

    public Formation save(Formation formation) {
        return formationDao.save(formation);
    }

    public void deleteById(Integer id) {
        formationDao.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return formationDao.existsById(id);
    }

    public List<Stagiaire> getStagiairesForFormation(Formation formation) {
        return formation.getInscriptions()
                .stream()
                .map(inscription -> inscription.getStagiaire())
                .collect(Collectors.toList());
    }

    public long countStagiairesInFormation(Formation formation) {
        return formation.getInscriptions() != null ? formation.getInscriptions().size() : 0;
    }

    public boolean isFormationComplete(Formation formation, int capaciteMax) {
        return countStagiairesInFormation(formation) >= capaciteMax;
    }
}
