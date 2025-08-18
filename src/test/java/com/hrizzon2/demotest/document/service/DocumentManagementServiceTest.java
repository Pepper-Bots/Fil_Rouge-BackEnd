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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentManagementServiceTest {

    @Mock
    private DocumentDao documentDao;
    @Mock
    private DossierService dossierService;
    @Mock
    private EvenementService evenementService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private StatutDocumentDao statutDocumentDao;
    @Mock
    private DocumentStorageService documentStorageService;
    @Mock
    private StagiaireDao stagiaireDao;
    @Mock
    private TypeDocumentValidator typeDocumentValidator;
    @Mock
    private DocumentMongoDao documentMongoDao;

    @InjectMocks
    private DocumentManagementService documentService;

    // ==========
    // HELPERS
    // ==========

    private Stagiaire mkStagiaire(Integer id, String lastName) {
        Stagiaire s = new Stagiaire();
        s.setId(id);
        s.setLastName(lastName);
        return s;
    }

    private StatutDocument mkStatut(String nom) {
        StatutDocument st = new StatutDocument();
        st.setNom(nom);
        return st;
    }

    private ArgumentMatcher<Document> hasGridFsId(String id) {
        return d -> d != null && id.equals(d.getUrlFichier());
    }

    // =======================
    // TESTS: uploadDocument()
    // =======================

    @Test
    void uploadDocument_success() throws Exception {
        // GIVEN
        Integer stagiaireId = 1;
        Formation formation = new Formation();
        formation.setId(7);
        TypeDocument type = TypeDocument.CV;
        MockMultipartFile fichier = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "bytes".getBytes());

        Stagiaire stagiaire = mkStagiaire(stagiaireId, "Dupont");

        when(stagiaireDao.findById(stagiaireId)).thenReturn(Optional.of(stagiaire));
        when(typeDocumentValidator.isTypeAutorise(formation, type)).thenReturn(true);
        when(documentDao.findByDossierStagiaireIdAndType(stagiaireId, type)).thenReturn(List.of()); // aucun existant
        when(documentStorageService.saveFile(fichier, "1", "Dupont")).thenReturn("gridfs-123");

        when(statutDocumentDao.findByNom("EN_ATTENTE")).thenReturn(Optional.of(mkStatut("EN_ATTENTE")));

        // save retourne l'entité enrichie (id simulé)
        when(documentDao.save(argThat(hasGridFsId("gridfs-123")))).thenAnswer(invocation -> {
            Document d = invocation.getArgument(0);
            d.setId(999);
            return d;
        });

        // DocMongo présent -> sera mis à jour + audit
        DocumentMongo mongo = new DocumentMongo();
        mongo.setId("gridfs-123");
        mongo.setAudit(new ArrayList<>());
        when(documentMongoDao.findById("gridfs-123")).thenReturn(Optional.of(mongo));

        // WHEN
        Document saved = documentService.uploadDocument(stagiaireId, fichier, type, formation);

        // THEN
        assertNotNull(saved);
        assertEquals(999, saved.getId());
        assertEquals("cv.pdf", saved.getNomFichier());
        assertEquals(TypeDocument.CV, saved.getType());
        assertEquals("gridfs-123", saved.getUrlFichier());
        assertEquals(stagiaire, saved.getStagiaire());
        assertEquals(formation, saved.getFormation());
        assertEquals("EN_ATTENTE", saved.getStatut().getNom());
        assertNotNull(saved.getDateDepot());

        verify(dossierService).creerOuAssocierDossier(saved, stagiaireId);
        verify(documentDao, times(1)).save(any(Document.class));
        verify(documentMongoDao, atLeastOnce()).save(any(DocumentMongo.class));
    }

    @Test
    void uploadDocument_ko_stagiaireIntrouvable() throws Exception {
        // GIVEN
        Integer stagiaireId = 404;
        when(stagiaireDao.findById(stagiaireId)).thenReturn(Optional.empty());

        MockMultipartFile fichier = new MockMultipartFile("f", "cni.pdf", "application/pdf", new byte[]{});
        // WHEN + THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> documentService.uploadDocument(stagiaireId, fichier, TypeDocument.PIECE_IDENTITE, new Formation()));
        assertEquals("Stagiaire introuvable", ex.getMessage());

        verifyNoInteractions(documentStorageService);
        verify(documentDao, never()).save(any());
    }

    @Test
    void uploadDocument_ko_typeNonAutorise() throws Exception {
        // GIVEN
        Integer stagiaireId = 1;
        when(stagiaireDao.findById(stagiaireId)).thenReturn(Optional.of(mkStagiaire(stagiaireId, "Dupont")));
        when(typeDocumentValidator.isTypeAutorise(any(Formation.class), eq(TypeDocument.DIPLOME_BAC_3))).thenReturn(false);

        MockMultipartFile fichier = new MockMultipartFile("f", "diplome.pdf", "application/pdf", new byte[]{1});
        // WHEN + THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> documentService.uploadDocument(stagiaireId, fichier, TypeDocument.DIPLOME_BAC_3, new Formation()));
        assertEquals("Type de document non autorisé pour cette formation", ex.getMessage());

        verifyNoInteractions(documentStorageService);
        verify(documentDao, never()).save(any());
    }

    @Test
    void uploadDocument_ko_dejaSoumisNonRejete() throws Exception {
        // GIVEN
        Integer stagiaireId = 1;
        when(stagiaireDao.findById(stagiaireId)).thenReturn(Optional.of(mkStagiaire(stagiaireId, "Dupont")));
        when(typeDocumentValidator.isTypeAutorise(any(), any())).thenReturn(true);

        Document deja = new Document();
        deja.setStatut(mkStatut("EN_ATTENTE"));
        when(documentDao.findByDossierStagiaireIdAndType(stagiaireId, TypeDocument.PIECE_IDENTITE))
                .thenReturn(List.of(deja));

        MockMultipartFile fichier = new MockMultipartFile("f", "PIECE_IDENTITE.pdf", "application/pdf", new byte[]{});
        // WHEN + THEN
        assertThrows(IllegalArgumentException.class,
                () -> documentService.uploadDocument(stagiaireId, fichier, TypeDocument.PIECE_IDENTITE, new Formation()));

        verify(documentStorageService, never()).saveFile(any(), any(), any());
        verify(documentDao, never()).save(any());
    }

    @Test
    void uploadDocument_ok_auditCreeSiNull() throws Exception {
        // GIVEN
        Integer stagiaireId = 1;
        when(stagiaireDao.findById(stagiaireId)).thenReturn(Optional.of(mkStagiaire(1, "Dupont")));
        when(typeDocumentValidator.isTypeAutorise(any(), any())).thenReturn(true);
        when(documentDao.findByDossierStagiaireIdAndType(eq(1), any())).thenReturn(List.of());
        when(documentStorageService.saveFile(any(), eq("1"), eq("Dupont"))).thenReturn("file-1");
        when(statutDocumentDao.findByNom("EN_ATTENTE")).thenReturn(Optional.of(mkStatut("EN_ATTENTE")));

        when(documentDao.save(any(Document.class))).thenAnswer(i -> {
            Document d = i.getArgument(0);
            d.setId(12);
            return d;
        });

        // DocMongo trouvé mais sans audit -> le service doit créer la liste et pousser une action
        DocumentMongo mongo = new DocumentMongo();
        mongo.setId("file-1");
        mongo.setAudit(null);
        when(documentMongoDao.findById("file-1")).thenReturn(Optional.of(mongo));

        MockMultipartFile fichier = new MockMultipartFile("f", "cv.pdf", "application/pdf", new byte[]{1});
        // WHEN
        Document saved = documentService.uploadDocument(1, fichier, TypeDocument.CV, new Formation());

        // THEN
        assertNotNull(saved);
        verify(documentMongoDao, atLeastOnce()).save(argThat(m -> m.getAudit() != null && !m.getAudit().isEmpty()));
    }

    // =======================
    // TESTS: valider / rejeter
    // =======================

    @Test
    void validerDocument_success() {
        // GIVEN
        Integer id = 10;
        Document doc = new Document();
        doc.setId(id);
        doc.setUrlFichier("gridfs-123");
        doc.setStatut(mkStatut("EN_ATTENTE"));

        Stagiaire s = mkStagiaire(2, "Dupont");
        doc.setStagiaire(s);

        when(documentDao.findById(id)).thenReturn(Optional.of(doc));
        when(statutDocumentDao.findByNom("VALIDÉ")).thenReturn(Optional.of(mkStatut("VALIDÉ")));
        when(documentDao.save(any(Document.class))).thenAnswer(i -> i.getArgument(0));

        // côté Mongo
        DocumentMongo mongo = new DocumentMongo();
        mongo.setId("gridfs-123");
        mongo.setAudit(new ArrayList<>());
        when(documentMongoDao.findById("gridfs-123")).thenReturn(Optional.of(mongo));

        // WHEN
        Document res = documentService.validerDocument(id);

        // THEN
        assertEquals("VALIDÉ", res.getStatut().getNom());
        verify(dossierService, atMostOnce()).verifierEtMettreAJourStatut(anyInt());
        verify(notificationService).notifyStagiaireValidationDocument(2, 10, true);
        verify(documentMongoDao, atLeastOnce()).save(any(DocumentMongo.class));
    }

    @Test
    void rejeterDocument_success() {
        // GIVEN
        Integer id = 11;
        Document doc = new Document();
        doc.setId(id);
        doc.setUrlFichier("gridfs-999");
        doc.setStatut(mkStatut("EN_ATTENTE"));

        Stagiaire s = mkStagiaire(3, "Martin");
        doc.setStagiaire(s);

        when(documentDao.findById(id)).thenReturn(Optional.of(doc));
        when(statutDocumentDao.findByNom("REJETÉ")).thenReturn(Optional.of(mkStatut("REJETÉ")));
        when(documentDao.save(any(Document.class))).thenAnswer(i -> i.getArgument(0));

        // côté Mongo
        DocumentMongo mongo = new DocumentMongo();
        mongo.setId("gridfs-999");
        mongo.setAudit(new ArrayList<>());
        when(documentMongoDao.findById("gridfs-999")).thenReturn(Optional.of(mongo));

        // WHEN
        Document res = documentService.rejeterDocument(id);

        // THEN
        assertEquals("REJETÉ", res.getStatut().getNom());
        verify(notificationService).notifyStagiaireValidationDocument(3, 11, false);
        verify(documentMongoDao, atLeastOnce()).save(any(DocumentMongo.class));
    }

    @Test
    void testUploadDocumentSuccess() throws Exception {
        // GIVEN
        Integer stagiaireId = 1;
        Formation formation = new Formation();
        formation.setId(1);
        TypeDocument type = TypeDocument.CV;
        MockMultipartFile fichier = new MockMultipartFile(
                "cv", "cv.pdf", "application/pdf", "Contenu test".getBytes()
        );

        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setId(stagiaireId);
        stagiaire.setLastName("Dupont");

        when(stagiaireDao.findById(stagiaireId)).thenReturn(Optional.of(stagiaire));
        when(typeDocumentValidator.isTypeAutorise(formation, type)).thenReturn(true);
        when(documentDao.findByDossierStagiaireIdAndType(stagiaireId, type))
                .thenReturn(java.util.Collections.emptyList());
        when(documentStorageService.saveFile(fichier, stagiaireId.toString(), stagiaire.getLastName()))
                .thenReturn("fakeFileId");

        StatutDocument statut = new StatutDocument();
        statut.setNom("EN_ATTENTE");
        when(statutDocumentDao.findByNom("EN_ATTENTE")).thenReturn(Optional.of(statut));

        DocumentMongo documentMongo = new DocumentMongo();
        documentMongo.setId("fakeFileId");
        documentMongo.setAudit(new ArrayList<>());
        when(documentMongoDao.findById("fakeFileId")).thenReturn(Optional.of(documentMongo));

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

        verify(documentDao).save(any(Document.class));
        verify(dossierService).creerOuAssocierDossier(saved, stagiaireId);
        verify(documentStorageService).saveFile(fichier, stagiaireId.toString(), stagiaire.getLastName());
        verify(documentMongoDao, atLeastOnce()).findById("fakeFileId");
        verify(documentMongoDao, atLeastOnce()).save(any(DocumentMongo.class));
    }

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
