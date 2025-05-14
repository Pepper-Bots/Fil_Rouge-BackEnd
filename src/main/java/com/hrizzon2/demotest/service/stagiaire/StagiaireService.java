package com.hrizzon2.demotest.service.stagiaire;

import com.hrizzon2.demotest.dto.stagiaire.StagiaireDTO;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.Inscription;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.enums.StatutInscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StagiaireService {

    List<Stagiaire> findAll();

    Optional<Stagiaire> findById(Integer id);

    StagiaireDTO save(Stagiaire stagiaireDTO);

    void deleteById(Integer id);

    boolean existsById(Integer id);

    boolean existsByEmail(String email);

    List<Stagiaire> findByStatutInscription(StatutInscription statut);

    List<Stagiaire> findInscritsEntre(LocalDate debut, LocalDate fin);

    Inscription inscrireStagiaire(Stagiaire stagiaire, Formation formation);

}
