package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.dao.InscriptionDao;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.model.Inscription;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import com.hrizzon2.demotest.user.model.Stagiaire;
import com.hrizzon2.demotest.user.service.Stagiaire.StagiaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;

// TODO Service pas utilisé !!!

@Service
public class FormationInscriptionService {

    private final StagiaireService stagiaireService;
    private final InscriptionDao inscriptionDao;

    @Autowired
    public FormationInscriptionService(StagiaireService stagiaireService,
                                       InscriptionDao inscriptionDao) {
        this.stagiaireService = stagiaireService;
        this.inscriptionDao = inscriptionDao;
    }

    /**
     * Inscrit le stagiaire authentifié à la formation d’ID passé en paramètre.
     * On s’assure d’abord que le principal correspond bien à un stagiaire existant.
     */
    public Inscription inscrireFormationCourante(
            Principal principal,
            Formation formation) {

        // 1. On vérifie l’ID du stagiaire actuel
        Integer idStagiaire = stagiaireService.getIdFromPrincipal(principal);

        // 2. On peut éventuellement charger l’entité Stagiaire complète :
        Stagiaire stag = stagiaireService.findById(idStagiaire)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stagiaire non trouvé pour l’authentification en cours"));

        // 3. On crée l’inscription
        Inscription inscription = new Inscription();
        inscription.setStagiaire(stag);
        inscription.setFormation(formation);
        inscription.setDateInscription(LocalDate.now());
        inscription.setStatut(StatutInscription.EN_ATTENTE);

        // 4. On enregistre
        return inscriptionDao.save(inscription);
    }
}
