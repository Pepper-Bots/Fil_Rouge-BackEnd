package com.hrizzon2.demotest.evenement.dao;

import com.hrizzon2.demotest.evenement.model.Motif;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotifDao extends JpaRepository<Motif, Integer> {

    // Par défaut, JpaRepository fournit findAll(), findById(), save(), delete(), etc.
    // Ajoute ici si besoin des méthodes spécifiques, par exemple :

    List<Motif> findByLibelle(String libelle);
}

// TODO **Conserver** :
//  motifs d’évènement (ex: absence, retard).
//  => Motifs != selon type d'évènement

