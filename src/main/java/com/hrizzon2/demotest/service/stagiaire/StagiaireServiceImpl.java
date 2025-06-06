package com.hrizzon2.demotest.service.stagiaire;

import com.hrizzon2.demotest.dao.InscriptionDao;
import com.hrizzon2.demotest.dao.StagiaireDao;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.Inscription;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import com.hrizzon2.demotest.service.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Classe concrète qui fait vraiment le travail
// Cœur de la logique métier pour tout ce qui touche au Stagiaire.
// -> utilise le Dao pour la persistence
// -> utilise le Mapper pour la conversion entité/DTO
// -> gère les cas métiers (ex: inscription du stagiaire à une formation, existence par email, etc.)

// Très pratique pour factoriser tout le traitement (validation, mail d’activation, envoi du mot de passe initial, etc.).

@Service
public class StagiaireServiceImpl implements StagiaireService {

    private final StagiaireDao stagiaireDao;
    private final InscriptionDao inscriptionDao;
    private final EmailService emailService;

    @Autowired
    public StagiaireServiceImpl(StagiaireDao stagiaireDao, InscriptionDao inscriptionDao, EmailService emailService) {
        this.stagiaireDao = stagiaireDao;
        this.inscriptionDao = inscriptionDao;
        this.emailService = emailService;
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
    public Optional<Stagiaire> findByEmail(String email) {
        return stagiaireDao.findByEmail(email);
    }


    @Override
    @Transactional
    public Stagiaire save(Stagiaire stagiaire) { // La signature de la méthode doit correspondre à l'interface

        // 1. Initialisation éventuelle des champs
        stagiaire.setPremiereConnexion(true); // ou false selon mon workflow

        // 2. Génération d'un token d'activation (exemple UUID)
        String activationToken = UUID.randomUUID().toString();
        stagiaire.setActivationToken(activationToken); // Ajoute ce champ dans ton entité si besoin

        // 3. Sauvegarde de l'entité stagiaire dans la base de données
        Stagiaire savedStagiaire = stagiaireDao.save(stagiaire);

        // 4. Envoi du mail d'activation
        emailService.sendEmailValidationToken(savedStagiaire.getEmail(), activationToken);

        // 5. Retourne l'entité sauvegardée
        return savedStagiaire;
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
    public void inscrireStagiaire(Stagiaire stagiaire, Formation formation) {
        Inscription inscription = new Inscription();
        inscription.setStagiaire(stagiaire);
        inscription.setFormation(formation);
        inscription.setDateInscription(LocalDate.now());
        inscription.setStatut(StatutInscription.EN_ATTENTE);

        inscriptionDao.save(inscription);
    }

    /**
     * Extrait l'identifiant du Stagiaire actuellement authentifié, à partir du Principal.
     * <p>
     * On suppose que {@code principal.getName()} contient l'email du stagiaire.
     * On cherche ensuite en base l'entité Stagiaire correspondant à cet email
     * et on renvoie son identifiant (Integer).
     * </p>
     *
     * @param principal Principal fourni par Spring Security (dont getName() est l'email)
     * @return l'ID du Stagiaire (Integer)
     * @throws IllegalArgumentException si aucun Stagiaire n'est trouvé pour l'email fourni
     */
    @Override
    public Integer getIdFromPrincipal(Principal principal) {

        // Supposons que le principal.getName() renvoie l'email du stagiaire :
        String email = principal.getName();

        // Recherche du Stagiaire en base à partir de cet email
        Stagiaire stagiaire = stagiaireDao.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun stagiaire trouvé pour l'email : " + email));

        // On renvoie l'ID
        return stagiaire.getId();
    }


    @Override
    public Inscription getDerniereInscription(Stagiaire stagiaire) {
        List<Inscription> inscriptions = stagiaire.getInscriptions();
        if (inscriptions == null || inscriptions.isEmpty()) {
            return null;
        }
        return inscriptions.stream()
                .max(Comparator.comparing(Inscription::getDateInscription))
                .orElse(null);

        // Si vous souhaitez récupérer un Stagiaire depuis la BD avant d’appeler getDerniereInscription :
        // @Override
        // public Inscription getDerniereInscription(Integer stagiaireId) {
        //     Stagiaire s = stagiaireDao.findById(stagiaireId).orElseThrow(...);
        //     return getDerniereInscription(s);
        // }
    }

    @Override
    public StatutInscription getStatutDerniereInscriptionById(Integer stagiaireId) {
        Stagiaire s = stagiaireDao.findById(stagiaireId)
                .orElseThrow(() -> new IllegalArgumentException("Stagiaire introuvable"));
        Inscription dernier = getDerniereInscription(s);
        return (dernier == null) ? null : dernier.getStatut();
    }

}
