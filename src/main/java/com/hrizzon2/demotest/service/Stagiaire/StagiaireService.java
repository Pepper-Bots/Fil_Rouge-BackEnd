package com.hrizzon2.demotest.service.Stagiaire;

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

    List<Stagiaire> findAll();

    Optional<Stagiaire> findById(Integer id);

    Optional<Stagiaire> findByEmail(String email);

    Stagiaire save(Stagiaire stagiaire);

    void deleteById(Integer id);

    boolean existsById(Integer id);

    boolean existsByEmail(String email);

    List<Stagiaire> findByStatutInscription(StatutInscription statut);

    List<Stagiaire> findInscritsEntre(LocalDate debut, LocalDate fin);

    void inscrireStagiaire(Stagiaire stagiaire, Formation formation);

    Integer getIdFromPrincipal(Principal principal);

    /**
     * Renvoie l'objet Inscription dont la dateInscription est la plus récente
     * pour le stagiaire passé en paramètre. Null si aucune inscription existante.
     */
    Inscription getDerniereInscription(Stagiaire stagiaire);

    /**
     * Même logique, mais en partant de l'ID du stagiaire.
     * Renvoie le statut de la dernière inscription de ce stagiaire (ou null s’il n’y en a pas).
     */
    StatutInscription getStatutDerniereInscriptionById(Integer stagiaireId);

    void updatePhotoProfil(Integer id, String nomImage);
}
