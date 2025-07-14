package com.hrizzon2.demotest.user.service;

import com.hrizzon2.demotest.dao.InscriptionDao;
import com.hrizzon2.demotest.document.dao.DocumentDao;
import com.hrizzon2.demotest.document.dao.StatutDocumentDao;
import com.hrizzon2.demotest.document.model.StatutDocument;
import com.hrizzon2.demotest.dto.DocumentAttenteDto;
import com.hrizzon2.demotest.dto.InscriptionAttenteDto;
import com.hrizzon2.demotest.formation.dao.FormationDao;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import com.hrizzon2.demotest.user.dao.IntervenantDao;
import com.hrizzon2.demotest.user.dao.StagiaireDao;
import com.hrizzon2.demotest.user.dto.AdminDashboardDto;
import com.hrizzon2.demotest.user.dto.StagiaireDashboardDto;
import com.hrizzon2.demotest.user.model.Stagiaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private StagiaireDao stagiaireDao;

    @Autowired
    private FormationDao formationDao;

    @Autowired
    private IntervenantDao intervenantDao;

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private InscriptionDao inscriptionDao;

    @Autowired
    private StatutDocumentDao statutDocumentDao;

    public AdminDashboardDto getAdminDashboardStats() {
        AdminDashboardDto dto = new AdminDashboardDto();

        // Récupérer ET extraire l'entité StatutDocument
        StatutDocument statutEnAttente = statutDocumentDao.findByNom("EN_ATTENTE")
                .orElseThrow(() -> new RuntimeException("Statut EN_ATTENTE non trouvé en base"));

        // 1. Compteurs principaux
        dto.setNbStagiaires((int) stagiaireDao.count());
        dto.setNbFormations((int) formationDao.count());
        dto.setNbIntervenants((int) intervenantDao.count());

        // 2. Nombre de documents en attente - MAINTENANT ça marche
        dto.setNbDocsAttente(documentDao.countByStatut(statutEnAttente));

        // 3. Liste des inscriptions en attente (ex: dossiers incomplets)
        List<InscriptionAttenteDto> inscriptionsEnAttente = inscriptionDao
                .findByStatut(StatutInscription.valueOf("EN_ATTENTE"))
                .stream()
                .map(inscription -> {
                    Stagiaire stagiaire = inscription.getStagiaire();
                    InscriptionAttenteDto att = new InscriptionAttenteDto();
                    att.setStagiaireNom(stagiaire.getFirstName() + " " + stagiaire.getLastName());
                    att.setFormationNom(inscription.getFormation().getNom());
                    att.setStatutDossier(inscription.getStatut().toString());
                    return att;
                })
                .collect(Collectors.toList());
        dto.setInscriptionsEnAttente(inscriptionsEnAttente);

        // 4. Liste des documents à vérifier
        List<DocumentAttenteDto> docsAttente = documentDao
                .findByStatut(statutEnAttente)// doit retourner List<Document>
                .stream()
                .map(document -> {
                    DocumentAttenteDto doc = new DocumentAttenteDto();
                    doc.setNomFichier(document.getNomFichier());
                    doc.setStagiaireNom(document.getStagiaire().getFirstName() + " " + document.getStagiaire().getLastName());
                    doc.setUrlFichier(document.getUrlFichier());
                    return doc;
                })
                .collect(Collectors.toList());
        dto.setDocsAttente(docsAttente);

        return dto;
    }

    public StagiaireDashboardDto getStagiaireDashboardStats(Long idStagiaire) {
        StagiaireDashboardDto dto = new StagiaireDashboardDto();
        // Remplis avec les infos personnalisées pour le stagiaire
        return dto;
    }
}

// PAS OBLIGÉ de créer un contrôleur spécifique pour Intervenant
// si je passe par un service qui va requêter directement le nombre d’intervenants pour le dashboard
// (genre dans DashboardService).