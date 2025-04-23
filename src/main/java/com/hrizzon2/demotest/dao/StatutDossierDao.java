package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.StatutDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutDossierDao extends JpaRepository<StatutDossier, Integer> {
}
