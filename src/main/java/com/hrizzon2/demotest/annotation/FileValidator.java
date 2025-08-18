package com.hrizzon2.demotest.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Validateur de contrainte personnalisé pour la validation d'un fichier {@link MultipartFile}.
 * <p>
 * Ce validateur permet de :
 * <ul>
 *     <li>Vérifier que le type MIME du fichier correspond à une liste de types autorisés.</li>
 *     <li>Vérifier que la taille du fichier ne dépasse pas la limite spécifiée.</li>
 * </ul>
 * Les paramètres de validation (types acceptés, taille maximale) sont récupérés soit via l'annotation {@link ValidFile},
 * soit via la configuration centralisée {@link FileConfig}.
 * <p>
 * Ce validateur est utilisé en combinaison avec l'annotation {@link ValidFile} sur un champ de type {@link MultipartFile}.
 *
 * @author [TonNom]
 */
@Component
public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    /**
     * Liste des types MIME acceptés pour le fichier.
     */
    private List<String> acceptedTypes;

    /**
     * Taille maximale autorisée pour le fichier, en octets.
     */
    private long maxSize;

    /**
     * Configuration centralisée pour les paramètres par défaut d'upload de fichiers.
     */
    private final FileConfig fileConfig;

    /**
     * Constructeur avec injection de la configuration des fichiers.
     *
     * @param fileConfig Configuration des types MIME et autres paramètres par défaut.
     */
    @Autowired
    public FileValidator(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    /**
     * Initialise le validateur à partir des paramètres de l'annotation {@link ValidFile}.
     * <p>
     * Les types MIME et la taille maximale peuvent être précisés dans l'annotation ou récupérés
     * depuis la configuration centralisée.
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
     * Valide un fichier {@link MultipartFile} en contrôlant son type et sa taille.
     * <p>
     * Si le fichier est nul ou vide, la validation est considérée comme réussie (cas d'un champ facultatif).
     *
     * @param file    Fichier à valider.
     * @param context Contexte de validation (pour messages d'erreur personnalisés).
     * @return {@code true} si le fichier est valide ou vide, {@code false} sinon.
     */
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Si le fichier est facultatif ou vide
        }
        // Vérification du type de contenu et de la taille
        return FileValidationUtil.isValidFileSize(file, maxSize, context) &&
                FileValidationUtil.isValidFileType(file, acceptedTypes, context);
    }
}