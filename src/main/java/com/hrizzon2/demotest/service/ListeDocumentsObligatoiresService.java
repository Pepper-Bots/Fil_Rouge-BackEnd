package com.hrizzon2.demotest.service;

import com.hrizzon2.demotest.dao.ListeDocumentsObligatoiresDao;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.ListeDocumentsObligatoires;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListeDocumentsObligatoiresService {

    private final ListeDocumentsObligatoiresDao listeDocsDao;

    public ListeDocumentsObligatoiresService(ListeDocumentsObligatoiresDao listeDocsDao) {
        this.listeDocsDao = listeDocsDao;
    }

    /**
     * crée et sauvegarde un document pour une formation donnée.
     */
    @Transactional
    public ListeDocumentsObligatoires addRequiredDocument(Formation formation, TypeDocument typeDocument) {
        ListeDocumentsObligatoires entry = new ListeDocumentsObligatoires();
        entry.setFormation(formation);
        entry.setTypeDocument(typeDocument);
        return listeDocsDao.save(entry);
    }

    /**
     * permet de récupérer la liste des documents pour une formation.
     */
    public List<ListeDocumentsObligatoires> findByFormation(Formation formation) {
        return listeDocsDao.findByFormation(formation);
    }

}
