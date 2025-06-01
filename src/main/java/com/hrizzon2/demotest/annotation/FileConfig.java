package com.hrizzon2.demotest.annotation;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration centralisée pour les paramètres de gestion des fichiers de l'application.
 * <p>
 * Cette classe permet de définir et d'accéder à la liste des types de fichiers acceptés par défaut.
 * Les valeurs sont injectées automatiquement depuis le fichier de configuration de l'application
 * (ex : <code>application.properties</code> ou <code>application.yml</code>) à l'aide de la clé
 * <code>file.default.accepted.types</code>.
 * <p>
 * Utilisée notamment par les validateurs personnalisés pour contrôler les uploads.
 * <p>
 * Exemple d'entrée dans <code>application.properties</code> :
 * <pre>
 * file.default.accepted.types=image/jpeg,image/png,application/pdf
 * </pre>
 *
 * @author [TonNom]
 */
@Getter
@Configuration
public class FileConfig {

    /**
     * Liste des types MIME acceptés par défaut pour l'upload de fichiers,
     * injectée depuis la configuration de l'application.
     * -- GETTER --
     * Retourne la liste des types MIME acceptés par défaut pour les fichiers uploadés.
     *
     * @return Un tableau de chaînes contenant les types MIME acceptés.
     */
    @Value("${file.default.accepted.types}")
    private String[] defaultAcceptedTypes;

}