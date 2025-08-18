package com.hrizzon2.demotest.common.service;

import lombok.extern.slf4j.Slf4j;
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

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Service
@Slf4j
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
        try (InputStream in = fichier.getInputStream()) {
            uploadToLocalFileSystem(in, fileName, publicFile);
        }
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
        if (Files.notExists(storageDirectory)) {
            Files.createDirectories(storageDirectory);
        }
        Path destination = storageDirectory.resolve(fileName);
        Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        log.debug("Fichier stocké: {}", destination);
    }

    /**
     * Récupère une image stockée dans le dossier privé à partir de son nom.
     *
     * @param fileName Le nom du fichier à récupérer.
     * @return Le contenu du fichier sous forme de tableau d'octets.
     * @throws FileNotFoundException Si le fichier n'existe pas ou n'est pas accessible.
     */
    public byte[] getImageByName(String fileName) throws FileNotFoundException {
        Path destination = Paths.get(privateUploadFolder).resolve(fileName);
        try {
            if (Files.notExists(Path.of(path))) throw new FileNotFoundException("Fichier introuvable: " + fileName);
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    public Path getPrivateFilePath(String fileName) {
        return Paths.get(privateUploadFolder).resolve(fileName);
    }

    public void deleteFile(String fileName) {
        try {
            Path p = Paths.get(privateUploadFolder).resolve(fileName);
            if (Files.exists(p)) Files.delete(p);
        } catch (Exception e) {
            log.warn("Suppression fichier échouée: {}", fileName, e);
        }
    }
}
