package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.dao.*;
import com.hrizzon2.demotest.dto.AdminDashboardDto;
import com.hrizzon2.demotest.dto.DocumentAttenteDto;
import com.hrizzon2.demotest.dto.InscriptionAttenteDto;
import com.hrizzon2.demotest.dto.StagiaireDashboardDto;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.enums.StatutInscription;
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

    public AdminDashboardDto getAdminDashboardStats() {
        AdminDashboardDto dto = new AdminDashboardDto();

        // 1. Compteurs principaux
        dto.setNbStagiaires((int) stagiaireDao.count());
        dto.setNbFormations((int) formationDao.count());
        dto.setNbIntervenants((int) intervenantDao.count());

        // 2. Nombre de documents en attente (statut à adapter à ta base)
        dto.setNbDocsAttente(documentDao.countByStatut("EN_ATTENTE"));

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
                .findByStatutNom("EN_ATTENTE")// doit retourner List<Document>
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