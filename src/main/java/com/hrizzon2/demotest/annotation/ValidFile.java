package com.hrizzon2.demotest.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation de validation personnalisée pour contrôler le type MIME et la taille d’un ou plusieurs fichiers uploadés.
 * <p>
 * À placer sur un champ ou une propriété de type {@link org.springframework.web.multipart.MultipartFile}
 * ou {@link org.springframework.web.multipart.MultipartFile}[] dans un DTO ou une entité.
 * <br>
 * Permet de :
 * <ul>
 *     <li>Limiter les types MIME acceptés pour l’upload (ex : PDF, PNG, JPEG...)</li>
 *     <li>Limiter la taille maximale des fichiers</li>
 * </ul>
 * <b>Exemple d’utilisation :</b>
 * <pre>
 * {@code
 * @ValidFile(acceptedTypes = {"application/pdf", "image/jpeg"}, maxSize = 1048576)
 * private MultipartFile document;
 * }
 * </pre>
 * <p>
 * Les types et la taille maximale sont optionnels :
 * <ul>
 *     <li>Si <b>acceptedTypes</b> n’est pas renseigné, la configuration par défaut (FileConfig) est utilisée.</li>
 *     <li>Si <b>maxSize</b> vaut 0, aucune limitation de taille n’est appliquée.</li>
 * </ul>
 *
 * @author [TonNom]
 * @see com.hrizzon2.demotest.annotation.FileValidator
 * @see com.hrizzon2.demotest.annotation.ArrayFileValidator
 */
@Documented
@Constraint(validatedBy = {FileValidator.class, ArrayFileValidator.class})
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFile {

    /**
     * Message d’erreur par défaut à afficher en cas d’invalidation.
     *
     * @return Message d’erreur.
     */
    String message() default "Type de fichier invalide ou taille de fichier trop grande.";

    /**
     * Groupes de validation (usage avancé).
     */
    Class<?>[] groups() default {};

    /**
     * Payload pour porter des informations supplémentaires (usage avancé).
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Liste des types MIME acceptés pour le ou les fichiers uploadés.
     * Par défaut : utilise les types définis dans {@link FileConfig}.
     *
     * @return Tableau de types MIME autorisés.
     */
    String[] acceptedTypes() default {};

    /**
     * Taille maximale autorisée pour chaque fichier, en octets.
     * Par défaut : 0 (pas de limitation).
     *
     * @return Taille maximale (en octets); -1 pour désactiver la limite.
     */
    long maxSize() default -1; // taille maximum en octet (-1 signifie pas de limite de taille)
}