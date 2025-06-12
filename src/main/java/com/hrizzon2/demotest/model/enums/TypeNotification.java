package com.hrizzon2.demotest.model.enums;

/**
 * Types de notifications envoyées aux utilisateurs.
 */
public enum TypeNotification {

    INFORMATION,        // Notification d'information générale
    WARNING_ABSENCE,    // Alerte liée aux absences
    WARNING_DELAY,      // Alerte liée aux retards
    WARNING_ITEM,       // Alerte liée à un document refusé
    RAPPEL,             // Rappel de dossier incomplet ou autre action à faire
    CONFIRMATION        // Signale la validation d'un document
}

