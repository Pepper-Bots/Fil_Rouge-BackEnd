package com.hrizzon2.demotest.document.service;

import com.hrizzon2.demotest.document.dao.DocumentDao;
import com.hrizzon2.demotest.document.dao.DocumentMongoDao;
import com.hrizzon2.demotest.document.dao.StatutDocumentDao;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.document.model.DocumentMongo;
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

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private DocumentMongoDao documentMongoDao = mock(DocumentMongoDao.class);

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
                typeDocumentValidator,
                documentMongoDao
        );
    }

    @Test
    void testUploadDocumentSuccess() throws Exception {
        // GIVEN
        Integer stagiaireId = 1;
        Formation formation = new Formation();
        formation.setId(1); // ou autre valeur factice pour tester
        TypeDocument type = TypeDocument.CV;
        MockMultipartFile fichier = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "Contenu test".getBytes());

        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setId(stagiaireId);
        stagiaire.setLastName("Dupont");

        when(stagiaireDao.findById(stagiaireId)).thenReturn(Optional.of(stagiaire));
        when(typeDocumentValidator.isTypeAutorise(formation, type)).thenReturn(true);
        when(documentDao.findByDossierStagiaireIdAndType(stagiaireId, type)).thenReturn(java.util.Collections.emptyList());
        when(documentStorageService.saveFile(fichier, stagiaireId.toString(), stagiaire.getLastName())).thenReturn("fakeFileId");

        StatutDocument statut = new StatutDocument();
        statut.setNom("EN_ATTENTE");
        when(statutDocumentDao.findByNom("EN_ATTENTE")).thenReturn(Optional.of(statut));

        DocumentMongo documentMongo = new DocumentMongo();
        documentMongo.setId("fakeFileId");
        documentMongo.setAudit(new ArrayList<>());
        when(documentMongoDao.findById("fakeFileId")).thenReturn(Optional.of(documentMongo));
        when(documentMongoDao.save(any(DocumentMongo.class))).thenReturn(documentMongo);

        // ✅ CORRECTION : Mock du save avec réponse plus réaliste
        when(documentDao.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(999); // Simule l'ID généré par la DB
            return doc;
        });

        // WHEN
        Document saved = documentService.uploadDocument(stagiaireId, fichier, type, formation);

        // THEN
        assertNotNull(saved);
        assertEquals("cv.pdf", saved.getNomFichier());
        assertEquals(type, saved.getType());
        assertEquals("fakeFileId", saved.getUrlFichier());
        assertEquals(stagiaire, saved.getStagiaire());
        assertEquals(statut, saved.getStatut());
        assertEquals(formation, saved.getFormation());
        assertNotNull(saved.getDateDepot());

        // Vérifie que le document a bien été sauvegardé
        verify(documentDao).save(any(Document.class));
        verify(dossierService).creerOuAssocierDossier(saved, stagiaireId);
        verify(documentStorageService).saveFile(fichier, stagiaireId.toString(), stagiaire.getLastName());

        // ✅ VERIFICATION : Synchronisation MongoDB
        verify(documentMongoDao, atLeastOnce()).findById("fakeFileId");
        verify(documentMongoDao, atLeastOnce()).save(any(DocumentMongo.class));
    }


    @Test
    void testUploadDocumentStagiaireNotFound() throws Exception {
        // GIVEN
        int stagiaireId = 999;
        Formation formation = new Formation();
        TypeDocument type = TypeDocument.CV;
        MockMultipartFile fichier = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "Contenu test".getBytes());

        when(stagiaireDao.findById(stagiaireId)).thenReturn(Optional.empty());

        // WHEN & THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            documentService.uploadDocument(stagiaireId, fichier, type, formation);
        });

        assertEquals("Stagiaire introuvable", exception.getMessage());
        verify(documentStorageService, never()).saveFile(any(), any(), any());
        verify(documentDao, never()).save(any());
    }

    @Test
    void testUploadDocumentTypeNonAutorise() throws Exception {
        // GIVEN
        int stagiaireId = 1;
        Formation formation = new Formation();
        TypeDocument type = TypeDocument.DIPLOME_BAC_3;
        MockMultipartFile fichier = new MockMultipartFile("diplome", "diplome.pdf", "application/pdf", "Contenu test".getBytes());

        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setId(stagiaireId);
        stagiaire.setLastName("Dupont");

        when(stagiaireDao.findById(stagiaireId)).thenReturn(Optional.of(stagiaire));
        when(typeDocumentValidator.isTypeAutorise(formation, type)).thenReturn(false);

        // WHEN & THEN - Test qu'une exception est bien lancée
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            documentService.uploadDocument(stagiaireId, fichier, type, formation);
        });

        // Vérification du message d'erreur
        assertEquals("Type de document non autorisé pour cette formation", exception.getMessage());

        // Vérification que les services ne sont PAS appelés en cas d'erreur
        verify(documentStorageService, never()).saveFile(any(), any(), any());
        verify(documentDao, never()).save(any());
    }

    // TODO A RELIRE POUR COMPRENDRE :

    @Test
    void testValiderDocumentSuccess() {
        // GIVEN
        Integer documentId = 1;
        Document document = new Document();
        document.setId(documentId);
        document.setUrlFichier("fakeFileId");

        StatutDocument statutEnAttente = new StatutDocument();
        statutEnAttente.setNom("EN_ATTENTE");
        document.setStatut(statutEnAttente);

        StatutDocument statutValide = new StatutDocument();
        statutValide.setNom("VALIDÉ");

        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setId(1);
        document.setStagiaire(stagiaire);

        when(documentDao.findById(documentId)).thenReturn(Optional.of(document));
        when(statutDocumentDao.findByNom("VALIDÉ")).thenReturn(Optional.of(statutValide));
        when(documentDao.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock DocumentMongo
        DocumentMongo documentMongo = new DocumentMongo();
        documentMongo.setId("fakeFileId");
        documentMongo.setAudit(new ArrayList<>());
        when(documentMongoDao.findById("fakeFileId")).thenReturn(Optional.of(documentMongo));

        // WHEN
        Document result = documentService.validerDocument(documentId);

        // THEN
        assertNotNull(result);
        assertEquals(statutValide, result.getStatut());

        verify(documentDao).save(document);
        verify(notificationService).notifyStagiaireValidationDocument(stagiaire.getId(), documentId, true);
        verify(documentMongoDao, atLeastOnce()).save(any(DocumentMongo.class));
    }
}

