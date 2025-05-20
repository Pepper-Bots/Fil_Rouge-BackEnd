package com.hrizzon2.demotest.service.stagiaire;

import com.hrizzon2.demotest.dao.InscriptionDao;
import com.hrizzon2.demotest.dao.StagiaireDao;
import com.hrizzon2.demotest.dto.stagiaire.StagiaireDTO;
import com.hrizzon2.demotest.mapper.StagiaireMapper;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.Inscription;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StagiaireServiceImpl implements StagiaireService {

    private final StagiaireDao stagiaireDao;
    private final InscriptionDao inscriptionDao;
    private final StagiaireMapper stagiaireMapper;

    @Autowired
    public StagiaireServiceImpl(StagiaireDao stagiaireDao, InscriptionDao inscriptionDao, StagiaireMapper stagiaireMapper) {
        this.stagiaireDao = stagiaireDao;
        this.inscriptionDao = inscriptionDao;
        this.stagiaireMapper = stagiaireMapper;
    }

    @Override
    public List<Stagiaire> findAll() {
        return stagiaireDao.findAll();
    }

    @Override
    public Optional<Stagiaire> findById(Integer id) {
        return stagiaireDao.findById(id);
    }

    @Override
    @Transactional
    public StagiaireDTO save(Stagiaire stagiaire) { // La signature de la méthode doit correspondre à l'interface
        // Conversion de l'entité Stagiaire en DTO (si nécessaire avant de sauvegarder)
        StagiaireDTO stagiaireDTO = stagiaireMapper.toDTO(stagiaire);

        // Conversion du DTO en entité Stagiaire pour la sauvegarde
        Stagiaire stagiaireEntity = stagiaireMapper.fromDTO(stagiaireDTO); // Utilise l'instance injectée

        // Sauvegarde de l'entité dans la base de données
        Stagiaire savedStagiaire = stagiaireDao.save(stagiaireEntity);

        // Conversion de l'entité Stagiaire sauvegardée en DTO et retour
        return stagiaireMapper.toDTO(savedStagiaire); // Utilise l'instance injectée
    }

    @Override
    public void deleteById(Integer id) {
        stagiaireDao.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return stagiaireDao.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return stagiaireDao.findAll()
                .stream()
                .anyMatch(s -> s.getEmail() != null && s.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public List<Stagiaire> findByStatutInscription(StatutInscription statut) {
        return stagiaireDao.findAll()
                .stream()
                .filter(s -> s.getInscriptions().stream().anyMatch(i -> i.getStatut().equals(statut)))
                .toList();
    }

    @Override
    public List<Stagiaire> findInscritsEntre(LocalDate debut, LocalDate fin) {
        return stagiaireDao.findAll()
                .stream()
                .filter(s -> s.getInscriptions().stream().anyMatch(i ->
                        i.getDateInscription() != null &&
                                !i.getDateInscription().isBefore(debut) &&
                                !i.getDateInscription().isAfter(fin)))
                .toList();
    }

    @Override
    public Inscription inscrireStagiaire(Stagiaire stagiaire, Formation formation) {
        Inscription inscription = new Inscription();
        inscription.setStagiaire(stagiaire);
        inscription.setFormation(formation);
        inscription.setDateInscription(LocalDate.now());
        inscription.setStatut(StatutInscription.EN_ATTENTE);

        return inscriptionDao.save(inscription);
    }
}
