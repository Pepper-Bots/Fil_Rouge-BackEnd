package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DossierDao extends JpaRepository<Dossier, Integer> {
}
