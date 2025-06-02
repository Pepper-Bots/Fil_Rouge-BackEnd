package com.hrizzon2.demotest.service;


// Ici logique métier pour croiser les documents attendus + les docs transmis
// → Car il va manipuler les documents réels transmis,
// et peut avoir besoin d’accéder à la liste des attendus via un autre service ou le DAO.

import com.hrizzon2.demotest.dao.PieceJointeStagiaireDao;
import com.hrizzon2.demotest.dao.StatutDocumentDao;
import com.hrizzon2.demotest.dto.DocumentStatutUpdateDto;
import com.hrizzon2.demotest.dto.DocumentSummaryDto;
import com.hrizzon2.demotest.model.*;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Service métier pour la gestion des documents transmis par les stagiaires.
 */
@Service
public class PieceJointeStagiaireService {

    private final ListeDocumentsObligatoiresService listeDocumentsObligatoiresService;
    private final PieceJointeStagiaireDao pieceJointeStagiaireDao;
    private final StatutDocumentDao statutDocumentDao;
    private final NotificationService notificationService;


    @Autowired
    public PieceJointeStagiaireService(ListeDocumentsObligatoiresService listeDocumentsObligatoiresService,
                                       PieceJointeStagiaireDao pieceJointeStagiaireDao,
                                       StatutDocumentDao statutDocumentDao,
                                       NotificationService notificationService) {
        this.listeDocumentsObligatoiresService = listeDocumentsObligatoiresService;
        this.pieceJointeStagiaireDao = pieceJointeStagiaireDao;
        this.statutDocumentDao = statutDocumentDao;
        this.notificationService = notificationService;
    }

    public List<DocumentSummaryDto> getStatutDocumentsDossier(Stagiaire stagiaire, Formation formation) {
        // 1. Les documents obligatoires attendus pour cette formation :
        List<ListeDocumentsObligatoires> attendus = listeDocumentsObligatoiresService.findByFormation(formation);

        // 2. Les documents transmis par le stagiaire pour cette formation :
        List<PieceJointeStagiaire> transmis = pieceJointeStagiaireDao.findByStagiaireIdAndFormationId(stagiaire.getId(), formation.getId());

        // 3. On assemble la réponse
        List<DocumentSummaryDto> result = new ArrayList<>();
        for (ListeDocumentsObligatoires attendu : attendus) {
            DocumentSummaryDto dto = new DocumentSummaryDto();
            dto.setTypeDocument(attendu.getTypeDocument());
            dto.setObligatoire(true);

            // On cherche si ce type de document a été transmis par le stagiaire :
            PieceJointeStagiaire docTransmis = transmis.stream()
                    .filter(doc -> doc.getTypeDocument() == attendu.getTypeDocument())
                    .findFirst()
                    .orElse(null);

            dto.setTransmis(docTransmis != null);
            if (docTransmis != null) {
                // On met le statut du document transmis
                dto.setFichier(docTransmis.getFichier());
                if (docTransmis.getStatutDocument() != null)
                    dto.setStatut(docTransmis.getStatutDocument().getNom());
                else
                    dto.setStatut("En attente");
            } else {
                dto.setFichier(null);
                dto.setStatut(null);
            }
            result.add(dto);
        }
        return result;
    }

    public boolean isDossierComplet(Stagiaire stagiaire, Formation formation) {
        List<DocumentSummaryDto> docs = getStatutDocumentsDossier(stagiaire, formation);
        // Complétude = tous transmis ET validés
        return docs.stream().allMatch(dto ->
                dto.isTransmis() && "Validé".equalsIgnoreCase(dto.getStatut())
        );
    }

    public PieceJointeStagiaire uploadPieceJointe(Stagiaire stagiaire, Formation formation, TypeDocument typeDocument, MultipartFile file) {
        // TODO: logique d’upload (stockage, création entité, sauvegarde en BDD…)
    }

    public List<PieceJointeStagiaire> getPiecesPourStagiaireEtFormation(Integer stagiaireId, Integer formationId) {
        // TODO: retourne la liste des pièces jointes d’un stagiaire pour une formation
    }

    public void deletePieceJointe(Integer pieceId) {
        // TODO: supprime la pièce jointe côté BDD (et éventuellement le fichier physique)
    }

    @Transactional
    public void updateStatutDocument(Integer documentId, DocumentStatutUpdateDto dto) {
        PieceJointeStagiaire document = pieceJointeStagiaireDao.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document non trouvé"));

        // Mise à jour du statut et du commentaire (si refus)
        StatutDocument newStatut = statutDocumentDao.findByNom(dto.getStatut())
                .orElseThrow(() -> new EntityNotFoundException("Statut introuvable"));
        document.setStatutDocument(newStatut);

        if ("Refusé".equalsIgnoreCase(dto.getStatut())) {
            document.setCommentaire(dto.getCommentaire()); // TODO est ce qu'on garde le champ commentaire dans l'entité ?
        } else {
            document.setCommentaire(null); // On efface l'ancien commentaire s'il y en avait un
        }

        pieceJointeStagiaireDao.save(document);

        // --- Notification in-app ---
        // À adapter : récupère l'id du stagiaire concerné (ex : document.getStagiaire().getId())
        notificationService.notifyStagiaireOnDocumentNotValidated(document.getStagiaire().getId());

        // --- Notification email (si tu ajoutes le service Mail) ---
        // mailService.sendDocumentRefusEmail(...);
    }


}
