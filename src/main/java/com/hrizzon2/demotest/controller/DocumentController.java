package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.annotation.ValidFile;
import com.hrizzon2.demotest.model.Document;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.security.IsStagiaire;
import com.hrizzon2.demotest.service.DocumentService;
import com.hrizzon2.demotest.service.FichierService;
import com.hrizzon2.demotest.service.stagiaire.StagiaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class DocumentController {

    private final DocumentService documentService;
    private final StagiaireService stagiaireService;
    private final FichierService fichierService;

    @Autowired
    public DocumentController(DocumentService documentService, StagiaireService stagiaireService, FichierService fichierService) {
        this.documentService = documentService;
        this.stagiaireService = stagiaireService;
        this.fichierService = fichierService;
    }

    @GetMapping("/document")
    public Document get() {
        Document doc = new Document();
        doc.setId(1); // exemple simple

        return doc;
    }

//    @PostMapping("/stagiaires/{id}/documents")
//    public ResponseEntity<?> uploadDocument(
//            @PathVariable Long id,
//            @ValidFile @RequestParam("file") MultipartFile file,
//            Principal principal) throws IOException {
//
//        // 1. Contrôle d'accès et validation
//        // 2. Appel au service métier
//        fichierService.uploadDocument(id, file);
//        return ResponseEntity.ok("Fichier envoyé !");
//    }

    @PostMapping("/stagiaire/{id}/upload")
    public ResponseEntity<?> uploadDocument(
            @PathVariable Long id,
            @ValidFile @RequestParam("file") MultipartFile file,
            @RequestParam("type") String typeDocument,
            Principal principal) {
        // Contrôle que le stagiaire connecté correspond à id
        Long userId = stagiaireService.getIdFromPrincipal(principal); // à adapter selon ta logique
        if (!userId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
        }
        try {
            // 1. Contrôle d'accès et validation
            // 2. Appel au service métier
            documentService.uploadDocument(id, file, typeDocument);
            return ResponseEntity.ok("Fichier envoyé !");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur d'upload");
        }
    }

    @GetMapping("/dossier/image/{idDossier}")
    @IsStagiaire
    public ResponseEntity<byte[]> getImageDossier(@PathVariable int idDossier) {

        Optional<Dossier> optional = dossierDao.findById(idDossier);

        if (optional.isPresent()) {

            String nomImage = optional.get().getNomImage();

            try {
                byte[] image = fichierService.getImageByName(nomImage);

                HttpHeaders enTete = new HttpHeaders();
                String mimeType = Files.probeContentType(new File(nomImage).toPath());
                enTete.setContentType(MediaType.valueOf(mimeType));

                return new ResponseEntity<>(image, enTete, HttpStatus.OK);

            } catch (FileNotFoundException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (IOException e) {
                System.out.println("Le test du mimetype a echoué");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    // TODO : ajouter ici d'autres méthodes :
    // POST pour envoyer un document
    // GET pour visualiser
    // PUT pour valider, etc.

}

