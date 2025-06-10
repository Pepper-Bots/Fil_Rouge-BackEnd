package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.model.Document;
import com.hrizzon2.demotest.service.ValidationDocumentService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST pour la validation des documents soumis par les stagiaires.
 * Seuls les admins peuvent valider ou rejeter les documents.
 */
@RestController
@RequestMapping("/api/validation-documents")
public class ValidationDocumentController {

    private final ValidationDocumentService validationDocumentService;

    @Autowired
    public ValidationDocumentController(ValidationDocumentService validationDocumentService) {
        this.validationDocumentService = validationDocumentService;
    }

    /**
     * Valide un document par son ID.
     */
    @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('RESPONSABLE')")
    @PostMapping("/{documentId}/valider")
    public ResponseEntity<Document> validerDocument(@PathVariable @NotNull Long documentId) {
        Document docValide = validationDocumentService.validerDocument(documentId);
        return ResponseEntity.ok(docValide);
    }

    /**
     * Rejette un document par son ID.
     */
    @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('RESPONSABLE')")
    @PostMapping("/{documentId}/rejeter")
    public ResponseEntity<Document> rejeterDocument(@PathVariable @NotNull Long documentId) {
        Document docRejete = validationDocumentService.rejeterDocument(documentId);
        return ResponseEntity.ok(docRejete);
    }
}
