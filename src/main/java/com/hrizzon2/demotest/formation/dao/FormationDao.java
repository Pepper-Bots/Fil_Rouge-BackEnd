package com.hrizzon2.demotest.formation.dao;

import com.hrizzon2.demotest.formation.model.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormationDao extends JpaRepository<Formation, Integer> {

    // Exemple : compter stagiaires par formation
    int countStagiairesById(Integer formationId);

//    findAll() pour la dropdown des formations
//    findById() pour récupérer une formation spécifique
}
