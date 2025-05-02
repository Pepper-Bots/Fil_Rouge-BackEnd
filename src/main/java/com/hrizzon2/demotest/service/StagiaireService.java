//Va servir à gérer la logique complexe liée aux stagiaires
package com.hrizzon2.demotest.service;

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
public class StagiaireService {

    private final StagiaireDao stagiaireDao;

    @Autowired
    public StagiaireService(StagiaireDao stagiaireDao) {
        this.stagiaireDao = stagiaireDao;
    }

    public List<Stagiaire> findAll() {
        return stagiaireDao.findAll();
    }

    public Optional<Stagiaire> findById(Integer id) {
        return stagiaireDao.findById(id);
    }

    public Stagiaire save(Stagiaire stagiaire) {
        return stagiaireDao.save(stagiaire);
    }

    public void deleteById(Integer id) {
        stagiaireDao.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return stagiaireDao.existsById(id);
    }

    public boolean existsByEmail(String email) {
        return stagiaireDao.findAll()
                .stream()
                .anyMatch(s -> s.getEmail() != null && s.getEmail().equalsIgnoreCase(email));
    }

    public List<Stagiaire> findByStatutInscription(StatutInscription statut) {
        return stagiaireDao.findAll()
                .stream()
                .filter(s -> s.getInscription() != null && s.getInscription().getStatut() == statut)
                .collect(Collectors.toList());
    }

    public List<Stagiaire> findInscritsEntre(LocalDate debut, LocalDate fin) {
        return stagiaireDao.findAll()
                .stream()
                .filter(s -> s.getInscription() != null &&
                        s.getInscription().getDateCreation() != null &&
                        !s.getInscription().getDateCreation().isBefore(debut) &&
                        !s.getInscription().getDateCreation().isAfter(fin))
                .collect(Collectors.toList());
    }

    public Stagiaire inscrireStagiaire(Stagiaire stagiaire, Formation formation) {
        Inscription inscription = new Inscription();
        inscription.setStagiaire(stagiaire);
        inscription.setFormation(formation);
        inscription.setDateCreation(LocalDate.now());
        inscription.setStatut(StatutInscription.EN_ATTENTE); // à adapter
        stagiaire.setInscription(inscription);

        return stagiaireDao.save(stagiaire);
    }
}
