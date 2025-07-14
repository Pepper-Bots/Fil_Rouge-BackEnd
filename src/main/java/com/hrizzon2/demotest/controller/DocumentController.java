package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.annotation.ValidFile;
import com.hrizzon2.demotest.dto.DocumentSummaryDto;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.formation.service.FormationService;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.service.DossierDocumentService;
import com.hrizzon2.demotest.service.DossierService;
import com.hrizzon2.demotest.service.EvenementDocumentService;
import com.hrizzon2.demotest.service.FichierService;
import com.hrizzon2.demotest.user.model.Stagiaire;
import com.hrizzon2.demotest.user.service.Stagiaire.StagiaireService;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.List;

/**
 * Contrôleur REST pour la gestion des documents rattachés à un dossier.
 * À privilégier : la nouvelle mécanique "PieceJointeStagiaire".
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DossierDocumentService dossierDocumentService;
    private final EvenementDocumentService evenementDocumentService;
    private final DossierService dossierService;
    private final FichierService fichierService;
    private final FormationService formationService;
    private final StagiaireService stagiaireService;

    @Autowired
    public DocumentController(DossierDocumentService dossierDocumentService,
                              EvenementDocumentService evenementDocumentService,
                              DossierService dossierService,
                              FichierService fichierService,
                              FormationService formationService,
                              StagiaireService stagiaireService) {
        this.dossierDocumentService = dossierDocumentService;
        this.evenementDocumentService = evenementDocumentService;
        this.formationService = formationService;
        this.stagiaireService = stagiaireService;
        this.dossierService = dossierService;
        this.fichierService = fichierService;
    }

    /**
     * Upload d’un document lié à un dossier.
     *
     * @param dossierId id du dossier cible
     * @param file      fichier envoyé
     * @param type      type du document (ex: CV, JUSTIFICATIF_DOMICILE)
     * @return message succès ou erreur
     */
    @PostMapping("/dossier/{dossierId}/upload")
    public ResponseEntity<?> uploadDocument(
            @PathVariable Integer dossierId,
            @ValidFile @RequestParam("file") MultipartFile file,
            @RequestParam("type") TypeDocument type
    ) {
        try {
            dossierDocumentService.uploadDocument(dossierId, file, type);
            return ResponseEntity.ok("Fichier envoyé !");
        } catch (RuntimeException e) {
            // Attraper une exception métier ou runtime levée par le service
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur d'upload : " + e.getMessage());
        }
    }

    /**
     * Upload d’un document justificatif pour un évènement (absence, retard).
     */
    @PostMapping("/evenement/{evenementId}/upload")
    public ResponseEntity<?> uploadDocumentEvenement(
            @PathVariable Integer evenementId,
            @ValidFile @RequestParam("file") MultipartFile file,
            @RequestParam("type") TypeDocument type) {
        try {
            evenementDocumentService.uploadDocument(evenementId, file, type);
            return ResponseEntity.ok("Justificatif envoyé !");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur d'upload : " + e.getMessage());
        }
    }

    /**
     * Récupère l’image associée à un dossier pour affichage ou téléchargement.
     *
     * @param idDossier id du dossier
     * @return contenu binaire avec type MIME adapté ou 404 si non trouvé
     */
    @GetMapping("/dossier/{idDossier}/image")
    public ResponseEntity<byte[]> getImageDossier(@PathVariable int idDossier) {

        Dossier dossier = dossierService.getById(idDossier);
        String nomImage = dossier.getNomImage();

        if (nomImage == null || nomImage.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            byte[] image = fichierService.getImageByName(nomImage);

            // On devine le type MIME à partir du fichier stocké sur le disque
            Path cheminImage = fichierService.getImagePath(nomImage); // À ajouter dans FichierService !
            String mimeType = Files.probeContentType(cheminImage);
            if (mimeType == null) mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // valeur par défaut
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(mimeType));
            return new ResponseEntity<>(image, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Liste des documents transmis par un stagiaire pour une formation (dossier).
     */
    @GetMapping("/stagiaire/{stagiaireId}/formation/{formationId}")
    public ResponseEntity<List<DocumentSummaryDto>> getDocumentsParStagiaireEtFormation(
            @PathVariable Integer stagiaireId,
            @PathVariable Integer formationId) {
        Stagiaire stagiaire = stagiaireService.findById(stagiaireId)
                .orElseThrow(() -> new EntityNotFoundException("Stagiaire non trouvé avec l'ID : " + stagiaireId));
        Formation formation = formationService.findById(formationId)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée avec l'ID : " + formationId));
        List<DocumentSummaryDto> statutDocs = dossierDocumentService.getStatutDocumentsDossier(stagiaire, formation);
        return ResponseEntity.ok(statutDocs);
    }

    /**
     * Suppression d’un document par son ID (dossier ou évènement).
     */
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Integer documentId) {
        // Tentative suppression dans dossierDocumentService, si pas trouvé, dans evenementDocumentService
        boolean deleted = dossierDocumentService.supprimerDocument(documentId);
        if (!deleted) {
            deleted = evenementDocumentService.supprimerDocument(documentId);
        }
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}



