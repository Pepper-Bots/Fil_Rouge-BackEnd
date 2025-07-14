package com.hrizzon2.demotest.user.dao;

import com.hrizzon2.demotest.model.PieceJointeStagiaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PieceJointeStagiaireDao extends JpaRepository<PieceJointeStagiaire, Integer> {

    List<PieceJointeStagiaire> findByStagiaireIdAndFormationId(Integer stagiaireId, Integer formationId);

    List<PieceJointeStagiaire> findByStagiaireId(Integer stagiaireId);
}
