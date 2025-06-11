package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Inscription;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InscriptionDao extends JpaRepository<Inscription, Integer> {

    Optional<Inscription> findByStagiaireIdAndStatut(Integer stagiaireId, StatutInscription statut);

}

// TODO **Conserver** :
//  inscriptions stagiaires-formations.