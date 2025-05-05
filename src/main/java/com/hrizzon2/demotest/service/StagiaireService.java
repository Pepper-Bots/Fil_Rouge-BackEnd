//Va servir à gérer la logique complexe liée aux stagiaires
package com.hrizzon2.demotest.service;

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

/**
 * Service pour la gestion métier des stagiaires.
 * Fournit les opérations complexes au-delà du CRUD simple.
 */
@Service
public class StagiaireService {

    private final StagiaireDao stagiaireDao;
    private final InscriptionDao inscriptionDao;

    @Autowired
    public StagiaireService(StagiaireDao stagiaireDao, InscriptionDao inscriptionDao) {
        this.stagiaireDao = stagiaireDao;
        this.inscriptionDao = inscriptionDao;
    }

    /**
     * Récupère tous les stagiaires.
     */
    public List<Stagiaire> findAll() {
        return stagiaireDao.findAll();
    }

    /**
     * Cherche un stagiaire par ID.
     */
    public Optional<Stagiaire> findById(Integer id) {
        return stagiaireDao.findById(id);
    }

    /**
     * Enregistre un stagiaire en base.
     */
    public Stagiaire save(Stagiaire stagiaire) {
        return stagiaireDao.save(stagiaire);
    }

    /**
     * Supprime un stagiaire par ID.
     */
    public void deleteById(Integer id) {
        stagiaireDao.deleteById(id);
    }

    /**
     * Vérifie si un stagiaire existe par ID.
     */
    public boolean existsById(Integer id) {
        return stagiaireDao.existsById(id);
    }

    /**
     * Vérifie si un stagiaire existe par email.
     */
    public boolean existsByEmail(String email) {
        return stagiaireDao.findAll()
                .stream()
                .anyMatch(s -> s.getEmail() != null && s.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Récupère les stagiaires par statut d’inscription.
     */
    public List<Stagiaire> findByStatutInscription(StatutInscription statut) {
        return stagiaireDao.findAll()
                .stream()
                .filter(s -> s.getInscriptions().stream().anyMatch(i -> i.getStatut().equals(statut)))
                .collect(Collectors.toList());
    }

    /**
     * Récupère les stagiaires inscrits entre deux dates.
     */
    public List<Stagiaire> findInscritsEntre(LocalDate debut, LocalDate fin) {
        return stagiaireDao.findAll()
                .stream()
                .filter(s -> s.getInscriptions().stream().anyMatch(i ->
                        i.getDateCreation() != null &&
                                !i.getDateCreation().isBefore(debut) &&
                                !i.getDateCreation().isAfter(fin)))
                .collect(Collectors.toList());
    }

    /**
     * Inscrit un stagiaire à une formation.
     */
    public Inscription inscrireStagiaire(Stagiaire stagiaire, Formation formation) {
        Inscription inscription = new Inscription();
        inscription.setStagiaire(stagiaire);
        inscription.setFormation(formation);
        inscription.setDateCreation(LocalDate.now());
        inscription.setStatut(StatutInscription.EN_ATTENTE); // à adapter

        return inscriptionDao.save(inscription);
    }
}
