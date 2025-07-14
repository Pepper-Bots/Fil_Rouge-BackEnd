package com.hrizzon2.demotest.inscription.dao;

import com.hrizzon2.demotest.inscription.model.Inscription;
import com.hrizzon2.demotest.inscription.model.enums.StatutInscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InscriptionDao extends JpaRepository<Inscription, Integer> {

    Optional<Inscription> findByStagiaireIdAndStatut(Integer stagiaireId, StatutInscription statut);

    List<Inscription> findByStatut(StatutInscription statut);
}

// TODO **Conserver** :
//  inscriptions stagiaires-formations.