//Service pour gérer les formations et vérifier la disponibilité, les horaires, etc.
package com.hrizzon2.demotest.formation.service;

import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.formation.dao.FormationDao;
import com.hrizzon2.demotest.formation.dto.FormationAvecStatutDto;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.inscription.dao.InscriptionDao;
import com.hrizzon2.demotest.user.dao.PieceJointeStagiaireDao;
import com.hrizzon2.demotest.user.model.PieceJointeStagiaire;
import com.hrizzon2.demotest.user.model.Stagiaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FormationService {

    private final FormationDao formationDao;
    private final InscriptionDao inscriptionDao;
    private final PieceJointeStagiaireDao pieceJointeStagiaireDao;


    @Autowired
    public FormationService(FormationDao formationDao, InscriptionDao inscriptionDao, PieceJointeStagiaireDao pieceJointeStagiaireDao) {
        this.formationDao = formationDao;
        this.inscriptionDao = inscriptionDao;
        this.pieceJointeStagiaireDao = pieceJointeStagiaireDao;
    }

    public List<Formation> findAll() {

        return formationDao.findAll();
    }

    public Optional<Formation> findById(Integer id) {

        return formationDao.findById(id);
    }

    public Formation save(Formation formation) {

        return formationDao.save(formation);
    }

    public void deleteById(Integer id) {

        formationDao.deleteById(id);
    }

    public boolean existsById(Integer id) {

        return formationDao.existsById(id);
    }

    public List<Stagiaire> getStagiairesForFormation(Formation formation) {
        return formation.getInscriptions()
                .stream()
                .map(inscription -> inscription.getStagiaire())
                .collect(Collectors.toList());
    }

    public long countStagiairesInFormation(Formation formation) {
        return formation.getInscriptions() != null ? formation.getInscriptions().size() : 0;
    }

    public boolean isFormationComplete(Formation formation, int capaciteMax) {
        return countStagiairesInFormation(formation) >= capaciteMax;
    }

    // ✅ Version correcte
    public List<Formation> findFormationsByStagiaire(Integer stagiaireId) {
        return inscriptionDao.findFormationsByStagiaire(stagiaireId);
    }

    public List<FormationAvecStatutDto> getFormationsAvecStatutDocuments(Integer stagiaireId) {
        // 1. Récupérer toutes les formations du stagiaire
        List<Formation> formations = findFormationsByStagiaire(stagiaireId);

        return formations.stream().map(formation -> {
            FormationAvecStatutDto dto = new FormationAvecStatutDto();

            // Infos de base de la formation
            dto.setId(formation.getId());
            dto.setNom(formation.getNom());
            dto.setDescription(formation.getDescription());

            // Documents requis pour cette formation (via votre méthode @Transient)
            List<TypeDocument> documentsRequis = formation.getListeDocumentsObligatoires();
            dto.setDocumentsRequis(documentsRequis);

            // Documents uploadés par le stagiaire pour cette formation
            List<PieceJointeStagiaire> documentsUploades =
                    pieceJointeStagiaireDao.findByStagiaireIdAndFormationId(stagiaireId, formation.getId());

            dto.setDocumentsUploades(documentsUploades.stream()
                    .map(PieceJointeStagiaire::getTypeDocument)
                    .collect(Collectors.toList()));

            // Calcul du pourcentage de complétion
            int nbRequis = documentsRequis.size();
            int nbUploades = documentsUploades.size();

            if (nbRequis > 0) {
                dto.setPourcentageCompletion((nbUploades * 100) / nbRequis);
            } else {
                dto.setPourcentageCompletion(100); // Aucun document requis = 100%
            }

            // Détermination du statut du dossier
            dto.setStatutDossier(determinerStatutDossier(documentsRequis, documentsUploades));

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * ✅ Méthode utilitaire pour déterminer le statut du dossier
     */
    private String determinerStatutDossier(List<TypeDocument> documentsRequis,
                                           List<PieceJointeStagiaire> documentsUploades) {
        if (documentsRequis.isEmpty()) {
            return "COMPLET"; // Aucun document requis
        }

        if (documentsUploades.isEmpty()) {
            return "INCOMPLET"; // Aucun document uploadé
        }

        // Vérifier si tous les documents requis sont uploadés
        List<TypeDocument> typesUploades = documentsUploades.stream()
                .map(PieceJointeStagiaire::getTypeDocument)
                .collect(Collectors.toList());

        boolean tousDocumentsUploades = documentsRequis.stream()
                .allMatch(typesUploades::contains);

        if (tousDocumentsUploades) {
            // Vérifier le statut de validation des documents
            boolean tousValides = documentsUploades.stream()
                    .allMatch(doc -> doc.getStatutDocument() != null &&
                            "VALIDE".equals(doc.getStatutDocument().getNom()));

            if (tousValides) {
                return "VALIDE";
            } else {
                return "EN_VALIDATION";
            }
        } else {
            return "INCOMPLET";
        }
    }

    /**
     * ✅ Méthode pour obtenir le statut d'un dossier spécifique
     */
    public FormationAvecStatutDto getStatutDossierFormation(Integer stagiaireId, Integer formationId) {
        Optional<Formation> formationOpt = findById(formationId);
        if (formationOpt.isEmpty()) {
            throw new RuntimeException("Formation non trouvée: " + formationId);
        }

        Formation formation = formationOpt.get();

        // Vérifier que le stagiaire est inscrit à cette formation
        List<Formation> formationsStagiaire = findFormationsByStagiaire(stagiaireId);
        boolean inscrit = formationsStagiaire.stream()
                .anyMatch(f -> f.getId().equals(formationId));

        if (!inscrit) {
            throw new RuntimeException("Stagiaire non inscrit à cette formation");
        }

        // Réutiliser la logique existante
        List<FormationAvecStatutDto> formations = getFormationsAvecStatutDocuments(stagiaireId);
        return formations.stream()
                .filter(f -> f.getId().equals(formationId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Erreur lors du calcul du statut"));
    }
}
