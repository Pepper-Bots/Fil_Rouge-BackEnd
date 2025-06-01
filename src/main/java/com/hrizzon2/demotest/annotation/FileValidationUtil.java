package com.hrizzon2.demotest.annotation;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Classe utilitaire fournissant des méthodes statiques pour la validation des fichiers uploadés.
 * <p>
 * Ces méthodes sont principalement utilisées par les validateurs personnalisés pour :
 * <ul>
 *     <li>Vérifier si le type MIME d'un fichier est accepté.</li>
 *     <li>Vérifier que la taille d'un fichier ne dépasse pas la limite autorisée.</li>
 *     <li>Ajouter des messages d'erreur personnalisés au contexte de validation.</li>
 * </ul>
 *
 * @author [TonNom]
 */
public class FileValidationUtil {

    /**
     * Vérifie que le type MIME du fichier est accepté.
     * <p>
     * Si le fichier est null ou vide, la validation est considérée comme réussie (cas des fichiers facultatifs).
     * En cas de type invalide, un message d'erreur personnalisé est ajouté au contexte.
     *
     * @param file          Fichier à valider.
     * @param acceptedTypes Liste des types MIME acceptés.
     * @param context       Contexte de validation permettant d'ajouter des messages d'erreur personnalisés.
     * @return {@code true} si le type du fichier est accepté ou si le fichier est vide ; {@code false} sinon.
     */
    public static boolean isValidFileType(MultipartFile file, List<String> acceptedTypes, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Si le fichier est facultatif ou vide
        }
        String contentType = file.getContentType();
        if (contentType == null || !acceptedTypes.contains(contentType)) {
            context.buildConstraintViolationWithTemplate(
                            "Extension de fichier invalide : '" + contentType + "'. acceptés : '" +
                                    String.join("', '", acceptedTypes) + "'")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }
        return true;
    }

    /**
     * Vérifie que la taille du fichier ne dépasse pas la taille maximale autorisée.
     * <p>
     * Si la taille est supérieure à la limite, un message d'erreur personnalisé est ajouté au contexte.
     *
     * @param file    Fichier à valider.
     * @param maxSize Taille maximale autorisée en octets (0 pour désactiver la limite).
     * @param context Contexte de validation permettant d'ajouter des messages d'erreur personnalisés.
     * @return {@code true} si la taille du fichier est valide ; {@code false} sinon.
     */
    public static boolean isValidFileSize(MultipartFile file, long maxSize, ConstraintValidatorContext context) {
        if (maxSize > 0 && file.getSize() > maxSize) {
            context.buildConstraintViolationWithTemplate("Taille de fichier trop grande : " + file.getSize() + ", max : " + maxSize)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }
        return true;
    }

}
