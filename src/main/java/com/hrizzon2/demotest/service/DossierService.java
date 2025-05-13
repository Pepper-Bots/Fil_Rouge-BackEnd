package com.hrizzon2.demotest.service;
// Logique métier

import com.hrizzon2.demotest.dao.DossierDao;
import com.hrizzon2.demotest.model.Admin;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.model.StatutDossier;
import com.hrizzon2.demotest.security.AppUserDetails;
import com.hrizzon2.demotest.security.ISecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public DossierService(DossierDao dossierDao, ISecurityUtils securityUtils) {
        this.dossierDao = dossierDao;
        this.securityUtils = securityUtils;
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
    public Dossier create(Dossier dossier, AppUserDetails userDetails) {

        // Définit l'admin créateur
        dossier.setCreateur((Admin) userDetails.getUser());

        // Définit le statut par défaut (en attente de validation)
        if (dossier.getStatutDossier() == null) {
            StatutDossier statut = new StatutDossier();
            statut.setId(1);
            dossier.setStatutDossier(statut);
        }

        // Force la création d'un nouveau dossier
        dossier.setId(null);

        // Ajoute la date de création
        dossier.setDateCreation(LocalDateTime.now());

        return dossierDao.save(dossier);
    }

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
