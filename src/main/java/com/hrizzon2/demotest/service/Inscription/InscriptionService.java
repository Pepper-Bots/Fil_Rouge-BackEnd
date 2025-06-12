package com.hrizzon2.demotest.service.Inscription;

import com.hrizzon2.demotest.model.Inscription;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface InscriptionService {

    // Service pour gérer les inscriptions et leurs règles métier (par exemple, validation, expiration, etc.).


    List<Inscription> getAllInscriptions();

    Optional<Inscription> getInscriptionById(Integer id);

    Inscription saveInscription(Inscription inscription);

    void deleteInscription(Integer id);


    /**
     * Crée une inscription pour un stagiaire et une formation donnés,
     * génère automatiquement un Dossier, et renvoie l’Inscription persistée.
     */
    Inscription creerInscriptionAvecDossier(Integer stagiaireId, Integer formationId);

    /**
     * Valide une inscription existante (id donné) : met à jour
     * statut -> VALIDEE, dateValidation = aujourd’hui, dateModification = aujourd’hui.
     */
    Inscription validerInscription(Integer inscriptionId);


//    // dans StagiaireService ou InscriptionService
//    public Inscription inscrirePremierStagiaire(Stagiaire stagiaire, Formation formation) {
//        Inscription insc = new Inscription();
//        insc.setStagiaire(stagiaire);
//        insc.setFormation(formation);
//        insc.setDateInscription(LocalDate.now());
//        insc.setStatut(StatutInscription.EN_ATTENTE);
//
//        Dossier dossier = new Dossier();
//        dossier.setNomStagiaire(stagiaire.getLastName() + " " + stagiaire.getFirstName());
//        dossier.setStatutDossier("EN_COURS");
//        dossier.setDerniereMiseAJour(LocalDateTime.now());
//        dossier.setStagiaire(stagiaire);
//
//        insc.setDossier(dossier);
//
//        // Comme cascade = CascadeType.ALL sur dossier,
//        // persister insc suffira à persister dossier aussi.
//        inscriptionDao.save(insc);
//        return insc;
//    }


}
