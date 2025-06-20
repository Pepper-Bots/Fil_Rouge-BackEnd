package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormationDao extends JpaRepository<Formation, Integer> {

    // Exemple : compter stagiaires par formation
    long countStagiairesById(Integer formationId);
}
