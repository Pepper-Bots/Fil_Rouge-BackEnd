package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvenementDao extends JpaRepository<Evenement, Integer> {


    List<Evenement> findByStagiaireId(Integer stagiaireId);

    // Par exemple, récupérer évènements par date, statut, etc.
}
// TODO **Conserver** :
//  évènements (absences, retards) liés à stagiaires.