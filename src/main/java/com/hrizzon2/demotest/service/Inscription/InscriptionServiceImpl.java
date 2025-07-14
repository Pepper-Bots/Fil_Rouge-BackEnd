package com.hrizzon2.demotest.service.Inscription;

import com.hrizzon2.demotest.dao.InscriptionDao;
import com.hrizzon2.demotest.dao.StatutDossierDao;
import com.hrizzon2.demotest.formation.dao.FormationDao;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.model.Inscription;
import com.hrizzon2.demotest.model.StatutDossier;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import com.hrizzon2.demotest.user.dao.AdminDao;
import com.hrizzon2.demotest.user.dao.StagiaireDao;
import com.hrizzon2.demotest.user.model.Stagiaire;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InscriptionServiceImpl implements InscriptionService {

    private final InscriptionDao inscriptionDao;
    private final StagiaireDao stagiaireDao;
    private final FormationDao formationDao;
    private final AdminDao adminDao;
    private final StatutDossierDao statutDossierDao;


    @Autowired
    public InscriptionServiceImpl(
            InscriptionDao inscriptionDao,
            StagiaireDao stagiaireDao,
            FormationDao formationDao,
            AdminDao adminDao, StatutDossierDao statutDossierDao) {
        this.inscriptionDao = inscriptionDao;
        this.stagiaireDao = stagiaireDao;
        this.formationDao = formationDao;
        this.adminDao = adminDao;
        this.statutDossierDao = statutDossierDao;
    }

    @Override
    public List<Inscription> getAllInscriptions() {
        return inscriptionDao.findAll();
    }

    @Override
    public Optional<Inscription> getInscriptionById(Integer id) {
        return inscriptionDao.findById(id);
    }

    @Override
    public Inscription saveInscription(Inscription inscription) {
        return inscriptionDao.save(inscription);
    }

    @Override
    public void deleteInscription(Integer id) {
        inscriptionDao.deleteById(id);
    }

    /**
     * Crée une nouvelle Inscription pour un Stagiaire et une Formation donnés,
     * en créant un Dossier associé automatiquement, puis persiste le tout.
     */
    @Override
    public Inscription creerInscriptionAvecDossier(Integer stagiaireId, Integer formationId) {
        Stagiaire stagiaire = stagiaireDao.findById(stagiaireId)
                .orElseThrow(() -> new IllegalArgumentException("Stagiaire introuvable"));
        Formation formation = formationDao.findById(formationId)
                .orElseThrow(() -> new IllegalArgumentException("Formation introuvable"));
        // Récupérer l'Admin qui crée le dossier (par ex. ID = 1)
        var admin = adminDao.findById(1).orElseThrow(() -> new IllegalArgumentException("Admin introuvable"));

        // 1) Construire le Dossier
        Dossier dossier = new Dossier();
        dossier.setCodeDossier("DOSS-" + System.currentTimeMillis());
        dossier.setStatutDossier(
                (StatutDossier) statutDossierDao.findByNomStatut("EN_COURS")
                        .orElseThrow(() -> new IllegalArgumentException("StatutDossier « EN_COURS » introuvable"))
        );
        dossier.setStagiaire(stagiaire);
        dossier.setFormation(formation);
        dossier.setCreateur(admin);
        // dateCreation, dateModification, derniereMiseAJour seront gérés par Dossier.@PrePersist

        // 2) Construire l’Inscription
        Inscription inscription = new Inscription();
        inscription.setDateInscription(LocalDate.now());
        inscription.setStatut(StatutInscription.EN_ATTENTE);
        inscription.setStagiaire(stagiaire);
        inscription.setFormation(formation);

        // 3) Lier le Dossier à l’Inscription
        inscription.setDossier(dossier);

        // 4) Sauvegarder (cascade ALL sur dossier => persistance automatique du dossier)
        return inscriptionDao.save(inscription);
    }

    @Override
    public Inscription validerInscription(Integer inscriptionId) {
        Inscription insc = inscriptionDao.findById(inscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscription introuvable"));
        insc.setStatut(StatutInscription.VALIDEE);
        insc.setDateValidation(LocalDate.now());
        insc.setDateModification(LocalDate.now());
        return inscriptionDao.save(insc);
    }
}

// todo
//  - creerInscriptionAvecDossier, validerInscription
//  Conserver