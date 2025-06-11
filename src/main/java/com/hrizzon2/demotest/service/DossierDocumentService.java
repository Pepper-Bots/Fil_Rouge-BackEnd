package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.dto.DocumentSummaryDto;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DossierDocumentService {


    public void uploadDocument(Integer dossierId, MultipartFile file, TypeDocument type) {

    }

    public List<DocumentSummaryDto> getStatutDocumentsDossier(Stagiaire stagiaire, Formation formation) {
        return null;
    }

    public boolean supprimerDocument(Integer documentId) {
        return false;
    }
}
