package com.hrizzon2.demotest.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Validateur de contrainte personnalisé pour la validation d'un tableau de fichiers ({@link MultipartFile[]}).
 * <p>
 * Ce validateur permet de :
 * <ul>
 *     <li>Contrôler les types MIME autorisés pour chaque fichier du tableau.</li>
 *     <li>Contrôler la taille maximale autorisée pour chaque fichier.</li>
 * </ul>
 * Il s'appuie sur la configuration des fichiers acceptés via {@link FileConfig}
 * et sur les paramètres de l'annotation {@link ValidFile}.
 * <p>
 * Ce validateur est utilisé en combinaison avec l'annotation {@link ValidFile}
 * sur un champ ou une propriété d'une classe à valider.
 *
 * @author [TonNom]
 */
public class ArrayFileValidator implements ConstraintValidator<ValidFile, MultipartFile[]> {

    /**
     * Liste des types MIME acceptés pour les fichiers.
     */
    private List<String> acceptedTypes;

    /**
     * Taille maximale autorisée pour chaque fichier, en octets.
     */
    private long maxSize;

    /**
     * Configuration centralisée des paramètres de fichiers (types par défaut...).
     */
    private final FileConfig fileConfig;

    /**
     * Constructeur permettant l'injection de la configuration des fichiers.
     *
     * @param fileConfig Configuration des fichiers acceptés (types, taille...).
     */
    @Autowired
    public ArrayFileValidator(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    /**
     * Initialise le validateur avec les paramètres fournis par l'annotation {@link ValidFile}.
     *
     * @param constraintAnnotation Annotation contenant les paramètres de validation.
     */
    @Override
    public void initialize(ValidFile constraintAnnotation) {
        String[] customTypes = constraintAnnotation.acceptedTypes();
        if (customTypes.length > 0) {
            this.acceptedTypes = Arrays.asList(customTypes);
        } else {
            this.acceptedTypes = Arrays.asList(fileConfig.getDefaultAcceptedTypes());
        }
        this.maxSize = constraintAnnotation.maxSize();
    }

    /**
     * Valide un tableau de fichiers en vérifiant le type MIME et la taille de chaque fichier non vide.
     *
     * @param files   Tableau de fichiers à valider.
     * @param context Contexte de validation (permet de remonter des messages d'erreur personnalisés).
     * @return {@code true} si tous les fichiers sont valides ou si le tableau est vide ou {@code null} ; {@code false} sinon.
     */
    @Override
    public boolean isValid(MultipartFile[] files, ConstraintValidatorContext context) {
        if (files == null || files.length == 0) {
            return true; // Si le tableau est vide ou null
        }
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                // Vérification du type de contenu et de la taille
                return FileValidationUtil.isValidFileSize(file, maxSize, context) &&
                        FileValidationUtil.isValidFileType(file, acceptedTypes, context);
            }
        }
        return true;
    }
}