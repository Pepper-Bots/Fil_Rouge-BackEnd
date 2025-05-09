package com.hrizzon2.demotest.service.stagiaire;

import com.hrizzon2.demotest.dao.InscriptionDao;
import com.hrizzon2.demotest.dao.StagiaireDao;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.Inscription;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StagiaireServiceImpl implements StagiaireService {

    private final StagiaireDao stagiaireDao;
    private final InscriptionDao inscriptionDao;

    @Autowired
    public StagiaireServiceImpl(StagiaireDao stagiaireDao, InscriptionDao inscriptionDao) {
        this.stagiaireDao = stagiaireDao;
        this.inscriptionDao = inscriptionDao;
    }

    @Override
    public List<Stagiaire> findAll() {
        return stagiaireDao.findAll();
    }

    @Override
    public Optional<Stagiaire> findById(Integer id) {
        return stagiaireDao.findById(id);
    }

    @Override
    public Stagiaire save(Stagiaire stagiaire) {
        return stagiaireDao.save(stagiaire);
    }

    @Override
    public void deleteById(Integer id) {
        stagiaireDao.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return stagiaireDao.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return stagiaireDao.findAll()
                .stream()
                .anyMatch(s -> s.getEmail() != null && s.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public List<Stagiaire> findByStatutInscription(StatutInscription statut) {
        return stagiaireDao.findAll()
                .stream()
                .filter(s -> s.getInscriptions().stream().anyMatch(i -> i.getStatut().equals(statut)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Stagiaire> findInscritsEntre(LocalDate debut, LocalDate fin) {
        return stagiaireDao.findAll()
                .stream()
                .filter(s -> s.getInscriptions().stream().anyMatch(i ->
                        i.getDateCreation() != null &&
                                !i.getDateCreation().isBefore(debut) &&
                                !i.getDateCreation().isAfter(fin)))
                .collect(Collectors.toList());
    }

    @Override
    public Inscription inscrireStagiaire(Stagiaire stagiaire, Formation formation) {
        Inscription inscription = new Inscription();
        inscription.setStagiaire(stagiaire);
        inscription.setFormation(formation);
        inscription.setDateCreation(LocalDate.now());
        inscription.setStatut(StatutInscription.EN_ATTENTE);

        return inscriptionDao.save(inscription);
    }
}
