package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.EtatDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtatDossierDao extends JpaRepository<EtatDossier, Integer> {
}
