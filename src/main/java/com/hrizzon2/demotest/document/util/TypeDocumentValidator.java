package com.hrizzon2.demotest.document.util;

import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.formation.dao.ListeDocumentsObligatoiresDao;
import com.hrizzon2.demotest.formation.model.Formation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TypeDocumentValidator {

    private final ListeDocumentsObligatoiresDao listeDocsDao;

    @Autowired
    public TypeDocumentValidator(ListeDocumentsObligatoiresDao listeDocsDao) {
        this.listeDocsDao = listeDocsDao;
    }

    /**
     * Vérifie si un type de document est autorisé pour une formation donnée.
     *
     * @param formation la formation concernée
     * @param type      le type de document à vérifier
     * @return true si autorisé, false sinon
     */
    public boolean isTypeAutorise(Formation formation, TypeDocument type) {
        return listeDocsDao.findByFormation(formation).stream()
                .anyMatch(item -> item.getTypeDocument() == type);
    }
}
