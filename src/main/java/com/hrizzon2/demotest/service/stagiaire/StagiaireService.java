package com.hrizzon2.demotest.service.stagiaire;

import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.Inscription;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.enums.StatutInscription;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Interface qui définit le contrat :
// Permet d'injecter le service par interface (+ souple pour les tests/mocks)
// permet de séparer la déclaration (ce qui est attendu) de l'implémentation (comment on fait )

public interface StagiaireService {

    Stagiaire save(Stagiaire stagiaire);

    Optional<Stagiaire> findById(Integer id);

    List<Stagiaire> findAll();

    Optional<Stagiaire> findByEmail(String email);

    void deleteById(Integer id);

    boolean existsById(Integer id);

    boolean existsByEmail(String email);


    List<Stagiaire> findByStatutInscription(StatutInscription statut);

    List<Stagiaire> findInscritsEntre(LocalDate debut, LocalDate fin);

    Inscription inscrireStagiaire(Stagiaire stagiaire, Formation formation);


    Integer getIdFromPrincipal(Principal principal);
}
