package com.hrizzon2.demotest.notification.service;

import com.hrizzon2.demotest.model.enums.NiveauDroit;
import com.hrizzon2.demotest.service.DossierService;
import com.hrizzon2.demotest.user.model.Admin;
import com.hrizzon2.demotest.user.model.enums.TypeAdmin;
import com.hrizzon2.demotest.user.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;

// logique métier pour déclencher les notifications automatiques en fonction des règles métier.

/**
 * Service métier responsable du déclenchement automatique des notifications
 * liées aux événements critiques du système, tels que la validation de documents,
 * les seuils d’absences et de retards atteints ou proches.
 *
 * <p>Ce service centralise la logique métier pour envoyer les bonnes notifications
 * aux utilisateurs concernés (stagiaires, administrateurs).</p>
 *
 * <p>Il s’appuie sur {@link NotificationService} pour la création et la sauvegarde
 * des notifications, ainsi que sur {@link DossierService} pour l’analyse des données
 * métier (absences, retards, dossiers).</p>
 *
 * <p>Implémenter l’appel à ce service dans les points-clés du workflow (validation
 * document, déclaration absence/retard, mises à jour des dossiers).</p>
 */
@Service
public class NotificationTriggerService {

    private final DossierService dossierService;  // service gérant dossiers, absences, retards
    private final NotificationService notificationService;
    private final AdminService adminService;

    public NotificationTriggerService(DossierService dossierService,
                                      NotificationService notificationService,
                                      AdminService adminService) {
        this.dossierService = dossierService;
        this.notificationService = notificationService;
        this.adminService = adminService;
    }

    /**
     * Traite l’événement de validation ou rejet d’un document.
     * Envoie une notification au stagiaire selon le résultat.
     *
     * @param stagiaireId identifiant du stagiaire concerné
     * @param valid       true si le document est validé, false sinon
     */
    public void onDocumentValidation(int stagiaireId, boolean valid) {
        if (valid) {
            notificationService.notifyStagiaireOnDocumentValidated(stagiaireId);
        } else {
            notificationService.notifyStagiaireOnDocumentNotValidated(stagiaireId);
        }
    }

    /**
     * Analyse les seuils d’absences et retards pour un stagiaire.
     * Envoie des notifications adaptées au stagiaire et aux admins
     * en cas de seuils atteints ou proches.
     *
     * @param stagiaireId identifiant du stagiaire à analyser
     */
    public void checkSeuilsAbsenceRetard(int stagiaireId) {

        // Récupération des compteurs d'absence et de retard
        int nbAbsences = dossierService.countAbsences(stagiaireId);
        int nbRetards = dossierService.countRetards(stagiaireId);

        // Seuils définis (exemple)
        boolean absenceSeuilAtteint = nbAbsences >= 50;
        boolean retardSeuilAtteint = nbRetards >= 30;
        boolean absenceSeuilProche = nbAbsences >= 40;
        boolean retardSeuilProche = nbRetards >= 25;

        // Types d’admin à notifier selon la règle métier (tu peux adapter la liste)
        List<TypeAdmin> adminsAbsence = List.of(TypeAdmin.ASSISTANT_VIE_SCOLAIRE);
        List<TypeAdmin> adminsRetard = List.of(TypeAdmin.RESPONSABLE_FORMATION);

        if (absenceSeuilAtteint) {
            notificationService.notifyStagiaireOnAbsenceThreshold(stagiaireId);
            notifyAdminsSeuilAtteint(stagiaireId, "Absence", adminsAbsence);
        } else if (absenceSeuilProche) {
            notificationService.notifyStagiaireOnAbsenceThreshold(stagiaireId);
            notifyAdminsSeuilProche(stagiaireId, "Absence", adminsAbsence);
        }

        if (retardSeuilAtteint) {
            notificationService.notifyStagiaireOnDelayThreshold(stagiaireId);
            notifyAdminsSeuilAtteint(stagiaireId, "Retard", adminsRetard);
        } else if (retardSeuilProche) {
            notificationService.notifyStagiaireOnDelayThreshold(stagiaireId);
            notifyAdminsSeuilProche(stagiaireId, "Retard", adminsRetard);
        }
    }


    // ----------------------------------------------------
    // Méthodes internes privées pour notifier les admins
    // Pour les seuils d'alerte d'absence / retard
    // (exemple : seuil atteint/proche)
    // ----------------------------------------------------

    /**
     * Notifie les administrateurs des types donnés qu’un seuil est atteint.
     *
     * @param stagiaireId identifiant du stagiaire concerné
     * @param type        type d’alerte ("Absence", "Retard", etc.)
     * @param typesAdmin  liste des types d’administrateurs à notifier
     */
    public void notifyAdminsSeuilAtteint(int stagiaireId, String type, List<TypeAdmin> typesAdmin) {
        for (TypeAdmin typeAdmin : typesAdmin) {
            List<Admin> admins = adminService.getAdminsByTypeAndNiveau(typeAdmin, NiveauDroit.ADMIN);
            admins.forEach(admin ->
                    notificationService.sendCustomAdminNotification(admin, stagiaireId, type, "Seuil atteint")
            );
        }
    }

    /**
     * Notifie les administrateurs des types donnés qu’un seuil est proche.
     *
     * @param stagiaireId identifiant du stagiaire concerné
     * @param type        type d’alerte ("Absence", "Retard", etc.)
     * @param typesAdmin  liste des types d’administrateurs à notifier
     */
    public void notifyAdminsSeuilProche(int stagiaireId, String type, List<TypeAdmin> typesAdmin) {
        for (TypeAdmin typeAdmin : typesAdmin) {
            List<Admin> admins = adminService.getAdminsByTypeAndNiveau(typeAdmin, NiveauDroit.ADMIN);
            admins.forEach(admin ->
                    notificationService.sendCustomAdminNotification(admin, stagiaireId, type, "Seuil proche")
            );
        }
    }
}

