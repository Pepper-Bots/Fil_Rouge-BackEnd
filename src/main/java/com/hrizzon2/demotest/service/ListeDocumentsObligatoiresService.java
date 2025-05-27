package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.dao.ListeDocumentsObligatoiresDao;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.ListeDocumentsObligatoires;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListeDocumentsObligatoiresService {

    @Autowired
    private ListeDocumentsObligatoiresDao listeDocsDao;

    @Transactional
    public ListeDocumentsObligatoires addRequiredDocument(Formation formation, TypeDocument typeDocument) {
        ListeDocumentsObligatoires entry = new ListeDocumentsObligatoires();
        entry.setFormation(formation);
        entry.setTypeDocument(typeDocument);
        return listeDocsDao.save(entry);
    }
}
