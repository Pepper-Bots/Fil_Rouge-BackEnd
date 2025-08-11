//package com.hrizzon2.demotest.document.controller;
//
//import com.hrizzon2.demotest.document.model.Document;
//import com.hrizzon2.demotest.document.service.ValidationDocumentService;
//import jakarta.validation.constraints.NotNull;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
/// **
// * Contrôleur REST pour la validation/rejet des documents par un administrateur.
// * Seuls les utilisateurs avec rôle ADMINISTRATEUR ou RESPONSABLE y ont accès.
// */
//@RestController
//@RequestMapping("/api/validation-documents")
//public class ValidationDocumentController {
//
//    private final ValidationDocumentService validationDocumentService;
//
//    @Autowired
//    public ValidationDocumentController(ValidationDocumentService validationDocumentService) {
//        this.validationDocumentService = validationDocumentService;
//    }
//
//    /**
//     * Valide un document identifié par son ID.
//     *
//     * @param documentId id du document à valider
//     * @return document mis à jour
//     */
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('RESPONSABLE')")
//    @PostMapping("/{documentId}/valider")
//    public ResponseEntity<Document> validerDocument(@PathVariable @NotNull Integer documentId) {
//        Document docValide = validationDocumentService.validerDocument(Long.valueOf((documentId)));
//        return ResponseEntity.ok(docValide);
//    }
//
//    /**
//     * Rejette un document identifié par son ID.
//     *
//     * @param documentId id du document à rejeter
//     * @return document mis à jour
//     */
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('RESPONSABLE')")
//    @PostMapping("/{documentId}/rejeter")
//    public ResponseEntity<Document> rejeterDocument(@PathVariable @NotNull Integer documentId) {
//        Document docRejete = validationDocumentService.rejeterDocument(Long.valueOf(documentId));
//        return ResponseEntity.ok(docRejete);
//    }
//}
//
