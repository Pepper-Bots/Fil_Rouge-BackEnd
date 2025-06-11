package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Motif;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotifDao extends JpaRepository<Motif, Integer> {

    // Par défaut, JpaRepository fournit findAll(), findById(), save(), delete(), etc.
    // Ajoute ici si besoin des méthodes spécifiques, par exemple :

    List<Motif> findByType(String type);
}

// TODO **Conserver** :
//  motifs d’évènement (ex: absence, retard).
//  => Motifs != selon type d'évènement

