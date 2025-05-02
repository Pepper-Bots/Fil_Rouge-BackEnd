package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscriptionDao extends JpaRepository<Inscription, Integer> {

    // Vous pouvez ajouter des méthodes personnalisées ici si nécessaire

}
