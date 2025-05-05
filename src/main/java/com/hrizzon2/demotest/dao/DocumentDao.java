package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentDao extends JpaRepository<Document, Integer> {

    Optional<Document> findDocumentByName(String name);
}
