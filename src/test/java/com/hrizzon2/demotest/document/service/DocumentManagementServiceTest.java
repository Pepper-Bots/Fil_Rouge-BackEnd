package com.hrizzon2.demotest.document.service;

import com.hrizzon2.demotest.document.dao.DocumentDao;
import com.hrizzon2.demotest.document.dao.StatutDocumentDao;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.StatutDocument;
import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.document.util.TypeDocumentValidator;
import com.hrizzon2.demotest.evenement.service.EvenementService;
import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.inscription.service.DossierService;
import com.hrizzon2.demotest.notification.service.NotificationService;
import com.hrizzon2.demotest.user.dao.StagiaireDao;
import com.hrizzon2.demotest.user.model.Stagiaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class DocumentManagementServiceTest {

    private DocumentDao documentDao = mock(DocumentDao.class);
    private DossierService dossierService = mock(DossierService.class);
    private EvenementService evenementService = mock(EvenementService.class);
    private NotificationService notificationService = mock(NotificationService.class);
    private StatutDocumentDao statutDocumentDao = mock(StatutDocumentDao.class);
    private DocumentStorageService documentStorageService = mock(DocumentStorageService.class);
    private StagiaireDao stagiaireDao = mock(StagiaireDao.class);
    private TypeDocumentValidator typeDocumentValidator = mock(TypeDocumentValidator.class);

    private DocumentManagementService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentManagementService(
                documentDao,
                dossierService,
                evenementService,
                notificationService,
                statutDocumentDao,
                documentStorageService,
                stagiaireDao,
                typeDocumentValidator
        );
    }

    @Test
    void testUploadDocumentSuccess() throws Exception {
        // GIVEN
        int stagiaireId = 1;
        Formation formation = new Formation();
        formation.setId(1); // ou autre valeur factice
        TypeDocument type = TypeDocument.CV;
        MockMultipartFile fichier = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "Contenu test".getBytes());

        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setId(stagiaireId);

        when(stagiaireDao.findById(stagiaireId)).thenReturn(Optional.of(stagiaire));
        when(typeDocumentValidator.isTypeAutorise(formation, type)).thenReturn(true);
        when(documentDao.findByDossierStagiaireIdAndType(stagiaireId, type)).thenReturn(java.util.Collections.emptyList());
        when(documentStorageService.saveFile(fichier)).thenReturn("fakeFileId");

        StatutDocument statut = new StatutDocument();
        statut.setNom("EN_ATTENTE");
        when(statutDocumentDao.findByNom("EN_ATTENTE")).thenReturn(Optional.of(statut));

        when(documentDao.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Document saved = documentService.uploadDocument(stagiaireId, fichier, type, formation);

        // THEN
        assertNotNull(saved);
        assertEquals("cv.pdf", saved.getNomFichier());
        assertEquals(type, saved.getType());
        assertEquals("fakeFileId", saved.getUrlFichier());
        assertEquals(stagiaire, saved.getStagiaire());

        // Vérifie que le document a bien été sauvegardé
        verify(documentDao).save(any(Document.class));
        verify(dossierService).creerOuAssocierDossier(saved, stagiaireId);
    }
}
