package com.hrizzon2.demotest.formation.dao;

import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.formation.model.ListeDocumentsObligatoires;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListeDocumentsObligatoiresDao extends JpaRepository<ListeDocumentsObligatoires, Integer> {

    // Pour pouvoir faire des requetes du type : “Pour la formation X, donne-moi la liste des documents à fournir”

    List<ListeDocumentsObligatoires> findByFormation(Formation formation);

}
