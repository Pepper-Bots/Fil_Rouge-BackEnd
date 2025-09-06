package com.hrizzon2.demotest.regression;

import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.document.service.DocumentManagementService;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.formation.service.FormationService;
import com.hrizzon2.demotest.user.model.Stagiaire;
import com.hrizzon2.demotest.user.service.Stagiaire.StagiaireService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de régression vérifiant le fonctionnement sans MongoDB.
 *
 * <p>Ce test valide que l'application continue de fonctionner
 * en mode dégradé lorsque MongoDB est indisponible, garantissant
 * la résilience du système en production.</p>
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.data.mongodb.host=nonexistent-host",
        "spring.data.mongodb.port=27018"
})
@Transactional
class DocumentUploadSansMongoDBTest {

    @Autowired
    private DocumentManagementService documentManagementService;

    @Autowired
    private StagiaireService stagiaireService;

    @Autowired
    private FormationService formationService;

    /**
     * Teste l'upload de document sans MongoDB disponible.
     *
     * <p>Vérifie que le système MySQL seul permet :</p>
     * <ul>
     *   <li>Upload et stockage de métadonnées</li>
     *   <li>Validation des documents</li>
     *   <li>Suivi des statuts de dossier</li>
     * </ul>
     */
    @Test
    void shouldUploadDocumentWithoutMongoDB() throws Exception {
        // Given : MongoDB indisponible (via TestPropertySource)
        Stagiaire stagiaire = stagiaireService.findById(5).orElseThrow();
        Formation formation = formationService.findById(1).orElseThrow();

        MockMultipartFile file = new MockMultipartFile(
                "cv", "cv-test.pdf", "application/pdf", "contenu test".getBytes());

        // When : Upload malgré MongoDB down
        // Le service doit gérer l'erreur MongoDB gracieusement
        assertDoesNotThrow(() -> {
            documentManagementService.uploadDocument(
                    stagiaire.getId(), file, TypeDocument.CV, formation);
        });

        // Then : Les fonctionnalités MySQL continuent
        List<Document> documents = documentManagementService
                .getDocumentsByStagiaireAndFormation(stagiaire.getId(), formation.getId());

        assertFalse(documents.isEmpty());
        assertTrue(documents.stream().anyMatch(d -> "cv-test.pdf".equals(d.getNomFichier())));
    }
}