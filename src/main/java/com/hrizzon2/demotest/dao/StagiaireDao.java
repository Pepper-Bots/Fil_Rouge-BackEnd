package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Stagiaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StagiaireDao extends JpaRepository<Stagiaire, Integer> {

    Optional<Stagiaire> findByNom(String nom);
}
