//package com.hrizzon2.demotest.controller;
//
//import com.hrizzon2.demotest.annotation.ValidFile;
//import com.hrizzon2.demotest.document.dto.DocumentStatutUpdateDto;
//import com.hrizzon2.demotest.document.dto.DocumentSummaryDto;
//import com.hrizzon2.demotest.formation.model.Formation;
//import com.hrizzon2.demotest.user.model.PieceJointeStagiaire;
//import com.hrizzon2.demotest.user.model.Stagiaire;
//import com.hrizzon2.demotest.document.model.enums.TypeDocument;
//import com.hrizzon2.demotest.formation.service.FormationService;
//import com.hrizzon2.demotest.user.service.PieceJointeStagiaireService;
//import com.hrizzon2.demotest.user.service.Stagiaire.Stagiaire.StagiaireService;
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
/// **
// * Contrôleur pour la gestion des documents transmis par les stagiaires.
// */
//@RestController
//@RequestMapping("/stagiaires")
//public class PieceJointeStagiaireController {
//
//    private final FormationService formationService;
//    private final StagiaireService stagiaireService;
//    private final PieceJointeStagiaireService pieceJointeStagiaireService;
//
//    /**
//     * Constructeur avec injection des dépendances.
//     *
//     * @param formationService            Service pour l'accès aux formations.
//     * @param stagiaireService            Service pour l'accès aux stagiaires.
//     * @param pieceJointeStagiaireService Service métier pour la logique de comparaison attendus/transmis.
//     */
//    @Autowired
//    public PieceJointeStagiaireController(FormationService formationService,
//                                          StagiaireService stagiaireService,
//                                          PieceJointeStagiaireService pieceJointeStagiaireService) {
//        this.formationService = formationService;
//        this.stagiaireService = stagiaireService;
//        this.pieceJointeStagiaireService = pieceJointeStagiaireService;
//    }
//
//    /**
//     * Upload d'une pièce jointe (document) transmis par un stagiaire pour une formation.
//     *
//     * @param stagiaireId  ID du stagiaire
//     * @param formationId  ID de la formation
//     * @param typeDocument type de document (enum TypeDocument)
//     * @param file         le fichier à uploader
//     * @return Le document enregistré (201 CREATED) ou 500 en cas d’erreur
//     */
//    @PostMapping("/stagiaire/{stagiaireId}/formation/{formationId}/upload")
//    public ResponseEntity<?> uploadPieceJointe(
//            @PathVariable Integer stagiaireId,
//            @PathVariable Integer formationId,
//            @RequestParam("type") String typeDocument,
//            @ValidFile @RequestParam("file") MultipartFile file
//    ) {
//        // 1. On récupère le stagiaire ou lève EntityNotFoundException si pas trouvé
//        Stagiaire stagiaire = stagiaireService.findById(stagiaireId)
//                .orElseThrow(() -> new EntityNotFoundException(
//                        "Stagiaire non trouvé avec l'ID : " + stagiaireId));
//
//        // 2. On récupère la formation ou lève EntityNotFoundException si pas trouvée
//        Formation formation = formationService.findById(formationId)
//                .orElseThrow(() -> new EntityNotFoundException(
//                        "Formation non trouvée avec l'ID : " + formationId));
//
//        // 3. Conversion du paramètre "typeDocument" en enum TypeDocument
//        TypeDocument enumType;
//        try {
//            enumType = TypeDocument.valueOf(typeDocument);
//        } catch (IllegalArgumentException e) {
//            throw new EntityNotFoundException(
//                    "Type de document invalide : " + typeDocument);
//        }
//
//        // 4. Appel du service métier pour stocker le fichier
//        PieceJointeStagiaire piece = pieceJointeStagiaireService
//                .uploadPieceJointe(stagiaire, formation, enumType, file);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(piece);
//    }
//
//    /**
//     * Liste toutes les pièces jointes transmises par un stagiaire, optionnellement filtrés par formation.
//     *
//     * @param stagiaireId ID du stagiaire
//     * @param formationId ID de la formation
//     * @return Liste des documents transmis (200 OK)
//     */
//    @GetMapping("/stagiaire/{stagiaireId}/formation/{formationId}")
//    public ResponseEntity<List<PieceJointeStagiaire>> getPiecesPourStagiaireEtFormation(
//            @PathVariable Integer stagiaireId,
//            @PathVariable Integer formationId) {
//
//        // (Attention : on ne vérifie pas ici que le stagiaire existe, mais on peut le faire)
//        // On laisse le service gérer l’éventuel cas où il n’y a pas de pièces : il retournera une liste vide.
//        List<PieceJointeStagiaire> list =
//                pieceJointeStagiaireService.getPiecesPourStagiaireEtFormation(stagiaireId, formationId);
//        return ResponseEntity.ok(list);
//    }
//
//    /**
//     * Supprime un document transmis par un stagiaire.
//     *
//     * @param pieceId ID de la pièce jointe à supprimer
//     * @return 204 No Content si tout s'est bien passé, ou 404 si l'ID n'existe pas
//     */
//    @DeleteMapping("/pieces/{pieceId}")
//    public ResponseEntity<Void> deletePieceJointe(@PathVariable Integer pieceId) {
//
//        // Le service lèvera EntityNotFoundException si l’ID n’existe pas
//        pieceJointeStagiaireService.deletePieceJointe(pieceId);
//        return ResponseEntity.noContent().build();
//    }
//
//    /**
//     * Retourne la liste croisée des documents attendus (par la formation) et transmis (par le stagiaire)
//     * avec leur statut (transmis, validé, etc.).
//     *
//     * @param stagiaireId ID du stagiaire
//     * @param formationId ID de la formation
//     * @return Liste des statuts de chaque document attendu
//     */
//    @GetMapping("/{stagiaireId}/formations/{formationId}/statut-documents")
//    public ResponseEntity<List<DocumentSummaryDto>> getStatutDocuments(
//            @PathVariable Integer stagiaireId,
//            @PathVariable Integer formationId) {
//
//        Stagiaire stagiaire = stagiaireService.findById(stagiaireId)
//                .orElseThrow(() -> new EntityNotFoundException(
//                        "Stagiaire non trouvé avec l'ID : " + stagiaireId));
//
//        Formation formation = formationService.findById(formationId)
//                .orElseThrow(() -> new EntityNotFoundException(
//                        "Formation non trouvée avec l'ID : " + formationId));
//
//        List<DocumentSummaryDto> statutDocs =
//                pieceJointeStagiaireService.getStatutDocumentsDossier(stagiaire, formation);
//        return ResponseEntity.ok(statutDocs);
//    }
//
//    /**
//     * Met à jour le statut d'un document (Validé / Refusé, etc.).
//     *
//     * @param documentId ID de la pièce jointe à mettre à jour
//     * @param dto        payload contenant le nouveau statut et l'éventuel commentaire
//     * @return 200 OK ou 404 si l’ID n’existe pas
//     */
//    @PatchMapping("/documents/{documentId}/statut")
//    public ResponseEntity<?> updateDocumentStatut(
//            @PathVariable Integer documentId,
//            @RequestBody DocumentStatutUpdateDto dto) {
//
//        // Le service lèvera EntityNotFoundException si l’ID n’existe pas
//        pieceJointeStagiaireService.updateStatutDocument(documentId, dto);
//        return ResponseEntity.ok().build();
//    }
//
//
//}
