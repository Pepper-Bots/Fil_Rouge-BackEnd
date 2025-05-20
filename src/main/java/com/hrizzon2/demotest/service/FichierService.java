package com.hrizzon2.demotest.service;

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

    public void uploadToLocalFileSystem(MultipartFile fichier, String fileName, boolean publicFile) throws IOException {
        uploadToLocalFileSystem(fichier.getInputStream(), fileName, publicFile);
    }

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


    public byte[] getImageByName(String nomImage) throws FileNotFoundException {

        Path destination = Paths.get(privateUploadFolder + "/" + nomImage);// retrieve the image by its name

        try {
            return IOUtils.toByteArray(destination.toUri());
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        }

    }

}