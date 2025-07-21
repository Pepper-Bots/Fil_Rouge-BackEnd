package com.hrizzon2.demotest.inscription.service;
// Logique métier

import com.hrizzon2.demotest.document.dao.StatutDocumentDao;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.StatutDocument;
import com.hrizzon2.demotest.formation.dao.ListeDocumentsObligatoiresDao;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.formation.model.ListeDocumentsObligatoires;
import com.hrizzon2.demotest.inscription.dao.DossierDao;
import com.hrizzon2.demotest.inscription.dao.StatutDossierDao;
import com.hrizzon2.demotest.inscription.model.Dossier;
import com.hrizzon2.demotest.inscription.model.StatutDossier;
import com.hrizzon2.demotest.security.AppUserDetails;
import com.hrizzon2.demotest.security.ISecurityUtils;
import com.hrizzon2.demotest.user.model.Admin;
import com.hrizzon2.demotest.user.model.Stagiaire;
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
        StatutDossier statutDossierDefaut = statutDossierDao.findByNomStatut("EN_ATTENTE").get();
        dossier.setStatutDossier(statutDossierDefaut);

        // Dates
        LocalDateTime now = LocalDateTime.now();
        dossier.setDateCreation(now);
        dossier.setDateModification(now);
        dossier.setDerniereMiseAJour(now);

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

    /**
     * Recalcule et met à jour le statut global du dossier selon l'état des documents associés.
     * Ex : si tous les documents obligatoires sont validés, le statut passe à "COMPLET".
     *
     * @param dossierId l'identifiant du dossier à vérifier
     */
    @Transactional
    public void verifierEtMettreAJourStatut(Integer dossierId) {
        // Implémentation :
        // - récupérer dossier + documents
        // - vérifier état des documents (validé / non validé)
        // - mettre à jour statut dossier et sauvegarder
        Dossier dossier = getById(dossierId);
        boolean allValid = dossier.getDocuments().stream()
                .allMatch(doc -> doc.getStatut().getNom().equalsIgnoreCase("VALIDÉ"));

        String nouveauStatut = allValid ? "COMPLET" : "INCOMPLET";
        StatutDossier statut = statutDossierDao.findByNomStatut(nouveauStatut)
                .orElseThrow(() -> new EntityNotFoundException("Statut '" + nouveauStatut + "' introuvable"));

        dossier.setStatutDossier(statut);
        dossier.setDerniereMiseAJour(LocalDateTime.now());

        dossierDao.save(dossier);
    }

    /**
     * Compte le nombre d'absences déclarées pour un stagiaire donné.
     *
     * @param stagiaireId l'identifiant du stagiaire
     * @return nombre total d'absences
     */
    public int countAbsences(int stagiaireId) {
        // Implémentation : requête DAO ou calcul métier sur les évènements absences
        return 0; // placeholder
    }


    /**
     * Compte le nombre de retards déclarés pour un stagiaire donné.
     *
     * @param stagiaireId l'identifiant du stagiaire
     * @return nombre total de retards
     */
    public int countRetards(int stagiaireId) {
        // Implémentation : requête DAO ou calcul métier sur les évènements retards
        return 0; // placeholder
    }

    @Transactional
    public void creerOuAssocierDossier(Document document, Integer stagiaireId) {
        Dossier dossier = dossierDao
                .findByStagiaireIdAndFormationId(stagiaireId, document.getFormation().getId()) // ou un champ équivalent
                .orElseGet(() -> {
                    Dossier nouveau = new Dossier();
                    nouveau.setStagiaire(document.getStagiaire());
                    nouveau.setFormation(document.getFormation());

                    StatutDossier statut = statutDossierDao.findByNomStatut("EN_ATTENTE")
                            .orElseThrow(() -> new EntityNotFoundException("Statut 'EN_ATTENTE' introuvable"));
                    nouveau.setStatutDossier(statut);

                    LocalDateTime now = LocalDateTime.now();
                    nouveau.setDateCreation(now);
                    nouveau.setDateModification(now);
                    nouveau.setDerniereMiseAJour(now);

                    return dossierDao.save(nouveau);
                });

        document.setDossier(dossier);
    }
}
