package com.hrizzon2.demotest.common.service;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FichierService {

    @Value("${public.upload.folder}")
    private String publicUploadFolder;

    @Value("${private.upload.folder}")
    private String privateUploadFolder;

    /**
     * Nettoie et sécurise le nom d'un fichier uploadé.
     *
     * @param fileName nom d'origine
     * @return nom sécurisé
     */
    public String sanitizeFileName(String fileName) {
        String cleanName = Paths.get(fileName).getFileName().toString();
        cleanName = cleanName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        if (cleanName.contains("..")) {
            throw new IllegalArgumentException("Nom de fichier invalide");
        }
        return cleanName;
    }

    /**
     * Upload un fichier dans le système local à partir d'un {@link MultipartFile}.
     *
     * @param fichier    Le fichier à uploader (provenant d'un formulaire ou d'une requête HTTP).
     * @param fileName   Le nom sous lequel le fichier sera stocké.
     * @param publicFile True pour stocker dans le dossier public, false pour le dossier privé.
     * @throws IOException Si une erreur survient lors de la lecture ou de l'écriture du fichier.
     */
    public void uploadToLocalFileSystem(MultipartFile fichier, String fileName, boolean publicFile) throws IOException {
        uploadToLocalFileSystem(fichier.getInputStream(), fileName, publicFile);
    }

    /**
     * Upload un fichier dans le système local à partir d'un flux d'entrée.
     *
     * @param inputStream Flux d'entrée du fichier à stocker.
     * @param fileName    Le nom du fichier une fois stocké.
     * @param publicFile  True pour stockage dans le dossier public, false pour privé.
     * @throws IOException Si une erreur survient lors de l'écriture du fichier.
     */
    public void uploadToLocalFileSystem(InputStream inputStream, String fileName, boolean publicFile) throws IOException {

        Path storageDirectory = Paths.get(publicFile ? publicUploadFolder : privateUploadFolder);

        if (!Files.exists(storageDirectory)) {
            try {
                Files.createDirectories(storageDirectory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Path destination = Paths.get(storageDirectory.toString() + "/" + fileName);

        Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);

    }

    /**
     * Récupère une image stockée dans le dossier privé à partir de son nom.
     *
     * @param nomImage Le nom du fichier image à récupérer.
     * @return Le contenu du fichier sous forme de tableau d'octets.
     * @throws FileNotFoundException Si le fichier n'existe pas ou n'est pas accessible.
     */
    public byte[] getImageByName(String nomImage) throws FileNotFoundException {

        Path destination = Paths.get(privateUploadFolder + "/" + nomImage);// retrieve the image by its name

        try {
            return IOUtils.toByteArray(destination.toUri());
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        }

    }

    public Path getImagePath(String nomImage) {
        return Paths.get(privateUploadFolder, nomImage);
    }

    public void deleteFile(String nomFichier) {
        try {
            Path fichierPath = Paths.get(privateUploadFolder, nomFichier);
            if (Files.exists(fichierPath)) {
                Files.delete(fichierPath);
            }
        } catch (Exception e) {
            // Log l'erreur mais ne pas faire échouer l'opération
            e.printStackTrace();
        }
    }
}
