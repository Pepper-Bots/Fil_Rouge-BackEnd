package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.model.Document;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class DocumentController {

    @GetMapping("/document")
    public Document get() {
        Document doc = new Document();
        doc.setId(1); // exemple simple

        return doc;
    }

    // TODO : ajouter ici d'autres méthodes :
    // POST pour envoyer un document
    // GET pour visualiser
    // PUT pour valider, etc.

}

