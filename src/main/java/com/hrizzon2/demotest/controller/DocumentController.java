package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.annotation.ValidFile;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import com.hrizzon2.demotest.service.DocumentService;
import com.hrizzon2.demotest.service.DossierService;
import com.hrizzon2.demotest.service.FichierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Contrôleur REST pour la gestion des documents rattachés à un dossier.
 * À privilégier : la nouvelle mécanique "PieceJointeStagiaire".
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class DocumentController {

    private final DocumentService documentService;
    private final DossierService dossierService;
    private final FichierService fichierService;

    @Autowired
    public DocumentController(DocumentService documentService,
                              DossierService dossierService,
                              FichierService fichierService) {
        this.documentService = documentService;
        this.dossierService = dossierService;
        this.fichierService = fichierService;
    }

    /**
     * Upload d’un document lié à un dossier (ex: pièce justificative).
     *
     * @param dossierId Id du dossier auquel rattacher le document
     * @param file      Fichier à transmettre
     * @param type      Type du document (ex: "CV", "JUSTIFICATIF_DOMICILE")
     */
    @PostMapping("/dossier/{dossierId}/upload")
    public ResponseEntity<?> uploadDocument(
            @PathVariable Integer dossierId,
            @ValidFile @RequestParam("file") MultipartFile file,
            @RequestParam("type") TypeDocument type
    ) {
        try {
            documentService.uploadDocument(dossierId, file, type);
            return ResponseEntity.ok("Fichier envoyé !");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur d'upload");
        }
    }

    // Contrôle que le stagiaire connecté correspond à id
//        Long userId = stagiaireService.getIdFromPrincipal(principal); // à adapter selon ta logique
//        if (!userId.equals(id)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
//        }
//        try {
//            // 1. Contrôle d'accès et validation
//            // 2. Appel au service métier
//            documentService.uploadDocument(id, file, typeDocument);
//            return ResponseEntity.ok("Fichier envoyé !");
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur d'upload");
//        }

    /**
     * Récupère l’image associée à un dossier (pour affichage ou téléchargement).
     *
     * @param idDossier Id du dossier dont on veut l’image
     * @return L’image en binaire avec le bon type MIME
     */
    @GetMapping("/dossier/{idDossier}/image")
    public ResponseEntity<byte[]> getImageDossier(@PathVariable int idDossier) {
        Dossier dossier = dossierService.getById(idDossier);

        // À toi d’adapter si le nom de l’image n’est pas stocké sous "nomImage" !
        String nomImage = dossier.getNomImage();
        if (nomImage == null || nomImage.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            byte[] image = fichierService.getImageByName(nomImage);

            // On devine le type MIME à partir du fichier stocké sur le disque
            Path cheminImage = fichierService.getImagePath(nomImage); // À ajouter dans FichierService !
            String mimeType = Files.probeContentType(cheminImage);
            if (mimeType == null) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // valeur par défaut
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(mimeType));
            return new ResponseEntity<>(image, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

}

// TODO
//  - uploadDocument (upload fichier lié à dossier ou évènement)
//  Conserver + Fusion
//  Centraliser upload / téléchargement / suppression documents
//
//  - getDocumentsByStagiaire / Dossier / Evenement


