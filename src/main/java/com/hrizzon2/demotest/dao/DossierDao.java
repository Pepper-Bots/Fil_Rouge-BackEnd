package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DossierDao extends JpaRepository<Dossier, Long> {

    Optional<Dossier> findDossierByStagiaire(String stagiaire);
}
