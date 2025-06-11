package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Ville;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VilleDao extends JpaRepository<Ville, Integer> {

    List<Ville> findByNomContainingIgnoreCase(String nom);
}