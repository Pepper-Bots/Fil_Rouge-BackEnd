package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.DocumentStagiaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentStagiaireDao extends JpaRepository<DocumentStagiaire, Integer> {

    List<DocumentStagiaire> findByStagiaireIdAndFormationId(Integer stagiaireId, Integer formationId);

    List<DocumentStagiaire> findByStagiaireId(Integer stagiaireId);
}
