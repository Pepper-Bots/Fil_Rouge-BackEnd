package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Document;
import com.hrizzon2.demotest.model.StatutDocument;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentDao extends JpaRepository<Document, Integer> {

    Optional<Document> findDocumentByName(String name);

    List<Document> findByStagiaireId(Long stagiaireId);

    List<Document> findByStagiaireIdAndType(Long stagiaireId, TypeDocument type);

    List<Document> findByStatut(StatutDocument statut);

    Collection<Object> findByDossierStagiaireIdAndType(long l, TypeDocument typeDocument);

    List<Document> findByDossierStagiaireId(Integer stagiaireId);
}
