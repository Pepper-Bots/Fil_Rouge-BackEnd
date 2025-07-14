package com.hrizzon2.demotest.inscription.controller;

import com.hrizzon2.demotest.inscription.model.Inscription;
import com.hrizzon2.demotest.inscription.model.enums.StatutInscription;
import com.hrizzon2.demotest.inscription.service.Inscription.InscriptionService;
import com.hrizzon2.demotest.user.service.Stagiaire.StagiaireService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/inscriptions")
public class InscriptionController {

    private final InscriptionService inscriptionService;
    private final StagiaireService stagiaireService;

    public InscriptionController(
            InscriptionService inscriptionService,
            StagiaireService stagiaireService
    ) {
        this.inscriptionService = inscriptionService;
        this.stagiaireService = stagiaireService;
    }

    /**
     * Crée une nouvelle inscription (statut EN_ATTENTE, création automatique d’un dossier).
     * Exemple d’appel : POST /api/inscriptions/nouvelle?stagiaireId=3&formationId=5
     */
    @PostMapping("/nouvelle")
    public ResponseEntity<Inscription> nouvelleInscription(
            @RequestParam("stagiaireId") Integer stagiaireId,
            @RequestParam("formationId") Integer formationId
    ) {
        Inscription insc = inscriptionService.creerInscriptionAvecDossier(stagiaireId, formationId);
        return ResponseEntity.ok(insc);
    }

    /**
     * Renvoie le statut de la dernière inscription d’un stagiaire donné.
     * Exemple d’appel : GET /api/inscriptions/dernier/3
     */
    @GetMapping("/dernier/{stagiaireId}")
    public ResponseEntity<StatutInscription> statutDerniereInscription(
            @PathVariable Integer stagiaireId
    ) {
        // Charge le Stagiaire (via StagiaireService) avant de demander son statut
        StatutInscription statut = stagiaireService.getStatutDerniereInscriptionById(stagiaireId);
        return ResponseEntity.ok(statut);
    }

    /**
     * Exemple de validation d’une inscription par l’admin.
     * Passe le statut à VALIDEE + dateValidation = aujourd’hui.
     * Exemple d’appel : PUT /api/inscriptions/valider/10
     */
    @PutMapping("/valider/{inscriptionId}")
    public ResponseEntity<Inscription> validerInscription(
            @PathVariable Integer inscriptionId
    ) {
        Inscription updated = inscriptionService.validerInscription(inscriptionId);
        return ResponseEntity.ok(updated);
    }
}

// TODO - createInscription, validerInscription, getStatutDerniereInscription
//  Conserver
//  Gestion inscriptions