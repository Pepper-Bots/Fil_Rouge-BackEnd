package com.hrizzon2.demotest.service;
// Logique métier

import com.hrizzon2.demotest.dao.DossierDao;
import com.hrizzon2.demotest.dao.ListeDocumentsObligatoiresDao;
import com.hrizzon2.demotest.dao.StatutDocumentDao;
import com.hrizzon2.demotest.dao.StatutDossierDao;
import com.hrizzon2.demotest.model.*;
import com.hrizzon2.demotest.security.AppUserDetails;
import com.hrizzon2.demotest.security.ISecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Ici, toute la logique métier propre : getById, save, update, delete...
// Gestion centralisée des erreurs EntityNotFoundException & autorisations.
// Service centralisé pour la gestion des dossiers
// Contient toute la logique métier et la gestion des autorisations
@Service
public class DossierService {

    // Dépendances injectées : DAO pour gérer les produits et un utilitaire de sécurité
    private final DossierDao dossierDao;
    private final ISecurityUtils securityUtils;
    private final ListeDocumentsObligatoiresDao listeDocsDao;
    private final StatutDocumentDao statutDocDao;
    private final StatutDossierDao statutDossierDao;

    @Autowired
    public DossierService(DossierDao dossierDao, ISecurityUtils securityUtils, ListeDocumentsObligatoiresDao listeDocsDao, StatutDocumentDao statutDocDao, StatutDossierDao statutDossierDao) {
        this.dossierDao = dossierDao;
        this.securityUtils = securityUtils;
        this.listeDocsDao = listeDocsDao;
        this.statutDocDao = statutDocDao;
        this.statutDossierDao = statutDossierDao;
    }

    /**
     * Récupère tous les dossiers.
     */
    @Transactional
    public List<Dossier> getAll() {
        return dossierDao.findAll();
    }

    /**
     * Récupère un dossier par son ID.
     * Lance une exception si le dossier n'existe pas.
     */
    @Transactional
    public Dossier getById(int id) {
        return dossierDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dossier introuvable avec l'id: " + id));
    }

    /**
     * Récupère les dossiers avec pagination.
     * Note: Cette méthode nécessite que DossierDao implémente ou hérite d'une interface
     * avec la méthode findAll(Pageable) retournant un org.springframework.data.domain.Page
     */
    @Transactional
    public List<Dossier> findDossiersPaginated(Pageable pageable) {
        // Si la pagination n'est pas directement supportée par votre DAO,
        // on peut implémenter une pagination manuelle ou retourner la liste complète
        List<Dossier> allDossiers = dossierDao.findAll();

        // Pagination manuelle basique (non optimale pour les grands volumes de données)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allDossiers.size());

        if (start >= allDossiers.size()) {
            return java.util.Collections.emptyList();
        }
        return allDossiers.subList(start, end);
    }

    /**
     * Vérifie si un dossier existe par son ID.
     */
    @Transactional
    public boolean existsById(Integer id) {
        return dossierDao.existsById(id);
    }

    /**
     * Recherche des dossiers par code.
     */
    @Transactional
    public Optional<Dossier> findByCodeDossier(String codeDossier) {
        return dossierDao.findByCodeDossier(codeDossier);
    }

    /**
     * Crée un nouveau dossier.
     */
    @Transactional
    public Dossier createWithRequiredDocuments(Stagiaire stagiaire, Formation formation, AppUserDetails userDetails) {
        Dossier dossier = new Dossier();

        // Créateur du dossier : admin (ou null si stagiaire, à adapter selon ton besoin)
        if (userDetails != null && userDetails.getUser() instanceof Admin) {
            dossier.setCreateur((Admin) userDetails.getUser());
        }


        dossier.setStagiaire(stagiaire);
        dossier.setFormation(formation);

        // Statut dossier par défaut (ex: "EN_ATTENTE" ou 1)
        StatutDossier statutDossierDefaut = statutDossierDao.findByNomStatut("EN_ATTENTE");
        dossier.setStatutDossier(statutDossierDefaut);

        // Dates
        LocalDateTime now = LocalDateTime.now();
        dossier.setDateCreation(now);
        dossier.setDateModification(now);
        dossier.setLastUpdated(now);

        // Génération automatique des documents obligatoires
        List<ListeDocumentsObligatoires> requiredDocs = listeDocsDao.findByFormation(formation);

        List<Document> documents = new ArrayList<>();
        Optional<StatutDocument> statutDocAFournir = statutDocDao.findByNom("À fournir");

        for (ListeDocumentsObligatoires item : requiredDocs) {
            Document doc = new Document();
            doc.setType(item.getTypeDocument());
            doc.setStatut(statutDocAFournir.orElseThrow(() ->
                    new EntityNotFoundException("Statut 'À fournir' introuvable"))
            );
            doc.setDossier(dossier);
            // Tu peux aussi initialiser d'autres champs ici (name, date, etc.)
            documents.add(doc);
        }
        dossier.setDocuments(documents);

        // Sauvegarde du dossier (cascade pour documents)
        return dossierDao.save(dossier);
    }
// => Le service fait le travail de savoir si c’est un Admin, et gère le champ créateur.
    // Plus besoin de caster côté contrôleur.

    /**
     * Met à jour un dossier existant.
     * Vérifie les autorisations avant la mise à jour.
     */
    @Transactional
    public Dossier update(int id, Dossier dossier, AppUserDetails userDetails) {
        Dossier existing = dossierDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dossier introuvable avec l'id: " + id));

        // Vérifie que l'utilisateur est autorisé à modifier ce dossier
        if (!isAuthorized(existing, userDetails)) {
            throw new SecurityException("Vous n'êtes pas authorisé à modifier le dossier");
        }

        // Préserve l'ID et le créateur original
        dossier.setId(id);
        dossier.setCreateur(existing.getCreateur());

        // Préserve la date de création originale
        if (existing.getDateCreation() != null) {
            dossier.setDateCreation(existing.getDateCreation());
        }

        // Met à jour la date de modification
        dossier.setDateModification(LocalDateTime.now());

        return dossierDao.save(dossier);
    }

    /**
     * Supprime un dossier.
     * Vérifie les autorisations avant la suppression.
     */
    @Transactional
    public void delete(int id, AppUserDetails userDetails) {
        Dossier existing = dossierDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dossier introuvable avec l'id: " + id));

        // Vérifie que l'utilisateur est autorisé à supprimer ce dossier
        if (!isAuthorized(existing, userDetails)) {
            throw new SecurityException(("Vous n'êtes pas authorisé à supprimé le dossier"));
        }
        dossierDao.deleteById(id);
    }

    /**
     * Vérifie si l'utilisateur est autorisé à modifier/supprimer un dossier.
     * Autorisé si l'utilisateur est admin ou s'il est le créateur du dossier.
     */
    private boolean isAuthorized(Dossier dossier, AppUserDetails userDetails) {
        String role = securityUtils.getRole(userDetails);
        return role.equals("ROLE_ADMIN") ||
                (dossier.getCreateur() != null &&
                        dossier.getCreateur().getId().equals(userDetails.getUser().getId()));
    }

}

// TODO  Implémentez une méthode dans DossierService pour recalculer le statutDossier
//  à chaque mise à jour d’un Document
//  (cascade “orphanRemoval” et “cascade=ALL” sur la relation documents).