package com.hrizzon2.demotest.document.model;

import com.hrizzon2.demotest.model.AuditAction;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

// entité MongoDB

@Getter
@Setter
@Document(collection = "documents")
public class DocumentMongo {
    @Id
    private String id;
    private String nomFichier;
    private String type;
    private long taille;
    private Date dateUpload;
    private String stagiaireId;
    private String nomStagiaire;
    private String statut;
    private String commentaire;
    private String cheminStorage;
    private List<AuditAction> audit;
    // getters/setters
}

