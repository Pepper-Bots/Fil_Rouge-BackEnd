package com.hrizzon2.demotest.formation.controller;

import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.formation.dao.FormationDao;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.formation.model.ListeDocumentsObligatoires;
import com.hrizzon2.demotest.formation.service.ListeDocumentsObligatoiresService;
import com.hrizzon2.demotest.security.IsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
public class ListeDocumentsObligatoiresController {

    private final FormationDao formationDao;
    private final ListeDocumentsObligatoiresService listeDocsService;

    @Autowired
    public ListeDocumentsObligatoiresController(FormationDao formationDao, ListeDocumentsObligatoiresService listeDocsService) {
        this.formationDao = formationDao;
        this.listeDocsService = listeDocsService;
    }

    /**
     * Méthode de création
     * → Crée un document obligatoire pour la formation spécifiée.
     */
    // POST /formations/{id}/documents-obligatoires
    @PostMapping("/{id}/documents-obligatoires")
    @IsAdmin
    public ResponseEntity<ListeDocumentsObligatoires> addRequiredDocument(
            @PathVariable Integer id,
            @RequestBody TypeDocumentPayload payload) {
        Formation formation = formationDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " + id));
        ListeDocumentsObligatoires created = listeDocsService.addRequiredDocument(formation, payload.getTypeDocument());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Méthode de liste
     * → Liste tous les documents obligatoires pour la formation spécifiée.
     */
    @GetMapping("/{id}/documents-obligatoires")
    public List<ListeDocumentsObligatoires> getDocumentsForFormation(@PathVariable Integer id) {
        Formation formation = formationDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " + id));
        return listeDocsService.findByFormation(formation);
    }

    // Classe DTO simple pour le payload :
    public static class TypeDocumentPayload {
        private TypeDocument typeDocument;

        public TypeDocument getTypeDocument() {
            return typeDocument;
        }

        public void setTypeDocument(TypeDocument typeDocument) {
            this.typeDocument = typeDocument;
        }
    }

}
