package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.model.enums.TypeDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EvenementDocumentService {
    public void uploadDocument(Integer evenementId, MultipartFile file, TypeDocument type) {
    }

    public boolean supprimerDocument(Integer documentId) {
    }
}
