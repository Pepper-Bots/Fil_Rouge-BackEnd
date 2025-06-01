package com.hrizzon2.demotest.service;


// Ici logique métier pour croiser les documents attendus + les docs transmis
// → Car il va manipuler les documents réels transmis,
// et peut avoir besoin d’accéder à la liste des attendus via un autre service ou le DAO.

import com.hrizzon2.demotest.dao.PieceJointeStagiaireDao;
import com.hrizzon2.demotest.dto.DocumentStatutDTO;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.ListeDocumentsObligatoires;
import com.hrizzon2.demotest.model.PieceJointeStagiaire;
import com.hrizzon2.demotest.model.Stagiaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service métier pour la gestion des documents transmis par les stagiaires.
 */
@Service
public class PieceJointeStagiaireService {

    private final ListeDocumentsObligatoiresService listeDocumentsObligatoiresService;
    private final PieceJointeStagiaireDao pieceJointeStagiaireDao;

    @Autowired
    public PieceJointeStagiaireService(ListeDocumentsObligatoiresService listeDocumentsObligatoiresService, PieceJointeStagiaireDao pieceJointeStagiaireDao) {
        this.listeDocumentsObligatoiresService = listeDocumentsObligatoiresService;
        this.pieceJointeStagiaireDao = pieceJointeStagiaireDao;
    }

    public List<DocumentStatutDTO> getStatutDocumentsDossier(Stagiaire stagiaire, Formation formation) {
        // 1. Les documents obligatoires attendus pour cette formation :
        List<ListeDocumentsObligatoires> attendus = listeDocumentsObligatoiresService.findByFormation(formation);

        // 2. Les documents transmis par le stagiaire pour cette formation :
        List<PieceJointeStagiaire> transmis = pieceJointeStagiaireDao.findByStagiaireIdAndFormationId(stagiaire.getId(), formation.getId());

        // 3. On assemble la réponse
        List<DocumentStatutDTO> result = new ArrayList<>();
        for (ListeDocumentsObligatoires attendu : attendus) {
            DocumentStatutDTO dto = new DocumentStatutDTO();
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
        List<DocumentStatutDTO> docs = getStatutDocumentsDossier(stagiaire, formation);
        // Complétude = tous transmis ET validés
        return docs.stream().allMatch(dto ->
                dto.isTransmis() && "Validé".equalsIgnoreCase(dto.getStatut())
        );
    }

}
