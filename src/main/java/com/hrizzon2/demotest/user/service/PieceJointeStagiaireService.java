package com.hrizzon2.demotest.user.service;


// Ici logique métier pour croiser les documents attendus + les docs transmis
// → Car il va manipuler les documents réels transmis,
// et peut avoir besoin d’accéder à la liste des attendus via un autre service ou le DAO.

import com.hrizzon2.demotest.dao.PieceJointeStagiaireDao;
import com.hrizzon2.demotest.dao.StatutDocumentDao;
import com.hrizzon2.demotest.dto.DocumentStatutUpdateDto;
import com.hrizzon2.demotest.dto.DocumentSummaryDto;
import com.hrizzon2.demotest.model.Formation;
import com.hrizzon2.demotest.model.ListeDocumentsObligatoires;
import com.hrizzon2.demotest.model.PieceJointeStagiaire;
import com.hrizzon2.demotest.model.StatutDocument;
import com.hrizzon2.demotest.model.enums.TypeDocument;
import com.hrizzon2.demotest.notification.NotificationService;
import com.hrizzon2.demotest.service.ListeDocumentsObligatoiresService;
import com.hrizzon2.demotest.user.model.Stagiaire;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Service métier pour la gestion des documents transmis par les stagiaires.
 */
@Service
public class PieceJointeStagiaireService {

    private final ListeDocumentsObligatoiresService listeDocumentsObligatoiresService;
    private final PieceJointeStagiaireDao pieceJointeStagiaireDao;
    private final StatutDocumentDao statutDocumentDao;
    private final NotificationService notificationService;

    // ---------- NOUVEAU : on injecte un chemin de stockage (dans application.properties) ----------
    @Value("${PUBLIC_UPLOAD_FOLDER}")
    private String uploadDir;

    @Autowired
    public PieceJointeStagiaireService(
            ListeDocumentsObligatoiresService listeDocumentsObligatoiresService,
            PieceJointeStagiaireDao pieceJointeStagiaireDao,
            StatutDocumentDao statutDocumentDao,
            NotificationService notificationService) {
        this.listeDocumentsObligatoiresService = listeDocumentsObligatoiresService;
        this.pieceJointeStagiaireDao = pieceJointeStagiaireDao;
        this.statutDocumentDao = statutDocumentDao;
        this.notificationService = notificationService;
    }

    public List<DocumentSummaryDto> getStatutDocumentsDossier(Stagiaire stagiaire, Formation formation) {
        // 1. Les documents obligatoires attendus pour cette formation :
        List<ListeDocumentsObligatoires> attendus = listeDocumentsObligatoiresService.findByFormation(formation);

        // 2. Les documents transmis par le stagiaire pour cette formation :
        List<PieceJointeStagiaire> transmis =
                pieceJointeStagiaireDao.findByStagiaireIdAndFormationId(stagiaire.getId(), formation.getId());

        // 3. On assemble la réponse
        List<DocumentSummaryDto> result = new ArrayList<>();
        for (ListeDocumentsObligatoires attendu : attendus) {
            DocumentSummaryDto dto = new DocumentSummaryDto();
            dto.setTypeDocument(attendu.getTypeDocument());
            dto.setObligatoire(true);

            // On cherche si ce type de document a été transmis par le stagiaire :
            PieceJointeStagiaire docTransmis = transmis.stream()
                    .filter(doc -> doc.getTypeDocument() == attendu.getTypeDocument())
                    .findFirst()
                    .orElse(null);

            dto.setTransmis(docTransmis != null);
            if (docTransmis != null) {
                // On met le statut du document transmis
                dto.setFichier(docTransmis.getCheminFichier()); // ici "fichier" = nom du fichier (ou chemin relatif)
                if (docTransmis.getStatutDocument() != null)
                    dto.setStatut(docTransmis.getStatutDocument().getNom());
                else
                    dto.setStatut("En attente");
            } else {
                dto.setFichier(null);
                dto.setStatut(null);
            }
            result.add(dto);
        }
        return result;
    }

    public boolean isDossierComplet(Stagiaire stagiaire, Formation formation) {
        List<DocumentSummaryDto> docs = getStatutDocumentsDossier(stagiaire, formation);
        // Complétude = tous transmis ET validés
        return docs.stream().allMatch(dto ->
                dto.isTransmis() && "Validé".equalsIgnoreCase(dto.getStatut())
        );
    }

    // ------------------ METHODE 1 : upload d’une pièce jointe ------------------

    // logique d’upload (stockage, création entité, sauvegarde en BDD…)
    @Transactional
    public PieceJointeStagiaire uploadPieceJointe(
            Stagiaire stagiaire,
            Formation formation,
            TypeDocument typeDocument,
            MultipartFile file) {
        try {
            // 1.1. Générer un nom unique pour éviter collisions
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
            // On récupère le nom original du MultipartFile (type String).
            //On « nettoie » ce nom (enlever les séquences dangereuses),
            //Puis on préfixe par System.currentTimeMillis() pour éviter toute collision si deux stagiaires uploadent le même fichier à la même milliseconde. // TODO -> vraiment utile ?

            // 1.2. Construire le dossier de stockage : <uploadDir>/<stagiaireId>_<formationId>/
            String subfolder = stagiaire.getId() + "_" + formation.getId();
            Path dossierTarget = Paths.get(uploadDir).resolve(subfolder);
            Files.createDirectories(dossierTarget); // crée le dossier si n’existe pas

            // 1.3. Enregistrer physiquement le fichier sur le disque
            Path filePath = dossierTarget.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);
            // file.getInputStream() lit les octets du fichier reçu en requête.
            //Files.copy(...) écrit ces octets dans le chemin physique <uploadDir>/<subfolder>/<uniqueFilename>.

            // 1.4. Créer l’entité et sauvegarder en BDD
            PieceJointeStagiaire piece = new PieceJointeStagiaire();
            piece.setStagiaire(stagiaire);
            piece.setFormation(formation);
            piece.setTypeDocument(typeDocument);
            piece.setCheminFichier(uniqueFilename);             // on stocke ici le nom de fichier
            piece.setCheminFichier(filePath.toString()); // (optionnel) chemin absolu si besoin (utile pour suppression)
            // on pourrait aussi stocker uniquement "subfolder/uniqueFilename" pour reconstruction ultérieure

            return pieceJointeStagiaireDao.save(piece);
            // On associe l’entité PieceJointeStagiaire aux objets Stagiaire, Formation et au type de document.
            //On renseigne fichier (pour l’affichage/nom uniquely), et cheminFichier (pour la suppression ultérieure).
            //Enfin, on fait un save(...) via le DAO pour persister en base.

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l’upload du fichier : " + e.getMessage(), e);
        }
    }

    // ------------------ METHODE 2 : lister les pièces jointes d’un stagiaire pour une formation ------------------

    // retourne la liste des pièces jointes d’un stagiaire pour une formation
    public List<PieceJointeStagiaire> getPiecesPourStagiaireEtFormation(
            Integer stagiaireId, Integer formationId) {

        // On part du principe que dao expose bien cette méthode (via une requête JPA custom)
        return pieceJointeStagiaireDao.findByStagiaireIdAndFormationId(stagiaireId, formationId);
    }

    // ------------------ METHODE 3 : suppression d’une pièce jointe ------------------

    // supprime la pièce jointe côté BDD (et éventuellement le fichier physique)
    public void deletePieceJointe(Integer pieceId) {
        PieceJointeStagiaire piece = pieceJointeStagiaireDao.findById(pieceId)
                .orElseThrow(() -> new EntityNotFoundException("Document non trouvé, id=" + pieceId));

        // 3.1. Suppression du fichier sur le disque (si jamais présent)
        String chemin = piece.getCheminFichier(); // on considère que l’entité a ce champ
        if (chemin != null) {
            File f = new File(chemin);
            if (f.exists()) {
                f.delete(); // supprime physiquement le fichier
            }
        }
        // 3.2. Suppression de l’enregistrement en base
        pieceJointeStagiaireDao.deleteById(pieceId);
    }

    @Transactional
    public void updateStatutDocument(Integer documentId, DocumentStatutUpdateDto dto) {
        PieceJointeStagiaire document = pieceJointeStagiaireDao.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document non trouvé"));

        // Mise à jour du statut et du commentaire (si refus)
        StatutDocument newStatut = statutDocumentDao.findByNom(dto.getStatut())
                .orElseThrow(() -> new EntityNotFoundException("Statut introuvable"));
        document.setStatutDocument(newStatut);

        if ("Refusé".equalsIgnoreCase(dto.getStatut())) {
            document.setCommentaire(dto.getCommentaire());
            // TODO est ce qu'on garde le champ commentaire dans l'entité ?
        } else {
            document.setCommentaire(null); // On efface l'ancien commentaire s'il y en avait un
        }

        pieceJointeStagiaireDao.save(document);

        // --- Notification in-app ---
        // À adapter : récupère l'id du stagiaire concerné (ex: document.getStagiaire().getId())
        notificationService.notifyStagiaireOnDocumentNotValidated(document.getStagiaire().getId());

        // --- Notification email (si tu ajoutes le service Mail) ---
        // mailService.sendDocumentRefusEmail(...);
    }


}
