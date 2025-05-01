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
import org.springframework.stereotype.Service;

import java.util.List;

// Ici, toute la logique métier propre : getById, save, update, delete...
// Gestion centralisée des erreurs EntityNotFoundException & autorisations.

@Service
public class DossierService {

    // Dépendances injectées : DAO pour gérer les produits et un utilitaire de sécurité
    private final DossierDao dossierDao;
    private final ISecurityUtils securityUtils;

    public DossierService(DossierDao dossierDao, ISecurityUtils securityUtils) {
        this.dossierDao = dossierDao;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public Dossier getById(int id) {
        return dossierDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dossier introuvable avec l'id: " + id));
    }

    @Transactional
    public List<Dossier> getAll() {
        return dossierDao.findAll();
    }

    @Transactional
    public Dossier create(Dossier dossier, AppUserDetails userDetails) {
        dossier.setCreateur((Admin) userDetails.getUser());
        if (dossier.getStatutDossier() == null) {
            StatutDossier statut = new StatutDossier();
            statut.setId(1);
            dossier.setStatutDossier(statut);
        }
        dossier.setId(null);
        return dossierDao.save(dossier);
    }

    @Transactional
    public Dossier update(int id, Dossier dossier, AppUserDetails userDetails) {
        Dossier existing = dossierDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dossier introuvable avec l'id: " + id));
        if (!isAuthorized(existing, userDetails)) {
            throw new SecurityException("Vous n'êtes pas authorisé à modifier le dossier");
        }
        dossier.setId(id);
        dossier.setCreateur(existing.getCreateur());
        return dossierDao.save(dossier);
    }

    @Transactional
    public void delete(int id, AppUserDetails userDetails) {
        Dossier existing = dossierDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dossier introuvable avec l'id: " + id));
        if (!isAuthorized(existing, userDetails)) {
            throw new SecurityException(("Vous n'êtes pas authorisé à supprimé le dossier"));
        }
        dossierDao.deleteById(id);
    }

    private boolean isAuthorized(Dossier dossier, AppUserDetails userDetails) {
        String role = securityUtils.getRole(userDetails);
        return role.equals("ROLE_ADMIN") ||
                dossier.getCreateur().getId().equals(userDetails.getUser().getId());
    }

}
