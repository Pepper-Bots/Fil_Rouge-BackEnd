package com.hrizzon2.demotest.user.controller;

import com.hrizzon2.demotest.document.dto.DocumentAttenteDto;
import com.hrizzon2.demotest.document.dto.DocumentValidationRequestDto;
import com.hrizzon2.demotest.inscription.dto.InscriptionAttenteDto;
import com.hrizzon2.demotest.user.dto.AdminDashboardDto;
import com.hrizzon2.demotest.user.dto.KpiDataDto;
import com.hrizzon2.demotest.user.dto.StagiaireDashboardDto;
import com.hrizzon2.demotest.user.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/dashboard")
    public AdminDashboardDto getAdminDashboard() {

        return dashboardService.getAdminDashboardStats();
    }

    @PreAuthorize("hasRole('STAGIAIRE')")
    @GetMapping("/stagiaire/dashboard/{id}")
    public StagiaireDashboardDto getStagiaireDashboard(@PathVariable Long id) {
        return dashboardService.getStagiaireDashboardStats(id);
    }

    /**
     * Récupère les KPIs du dashboard admin
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/kpis")
    public ResponseEntity<KpiDataDto> getKpis() {
        try {
            KpiDataDto kpis = dashboardService.calculerKpis();
            return ResponseEntity.ok(kpis);
        } catch (Exception e) {
            e.printStackTrace(); // ⚠️ Pour débug
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Récupère les inscriptions en attente
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/inscriptions/attente")
    public ResponseEntity<List<InscriptionAttenteDto>> getInscriptionsAttente(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<InscriptionAttenteDto> inscriptions = dashboardService.getInscriptionsEnAttente(limit);
            return ResponseEntity.ok(inscriptions);
        } catch (Exception e) {
            e.printStackTrace(); // ⚠️⚠ Debug
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Récupère les documents en attente
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/documents/attente")
    public ResponseEntity<List<DocumentAttenteDto>> getDocumentsAttente(
            @RequestParam(defaultValue = "15") int limit) {
        try {
            List<DocumentAttenteDto> documents = dashboardService.getDocumentsEnAttente(limit);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            e.printStackTrace(); // ⚠️⚠ Debug
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Télécharge un document
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/documents/{id}/download")
    public ResponseEntity<Resource> telechargerDocument(@PathVariable Long id) {
        try {
            // Implémentez cette méthode dans votre DashboardService
            Resource resource = dashboardService.telechargerDocument(id);
            String filename = dashboardService.getNomFichierDocument(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace(); // ⚠️⚠ Debug
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Valide un document
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/admin/documents/{id}/valider")
    public ResponseEntity<Map<String, String>> validerDocument(
            @PathVariable Long id,
            @RequestBody DocumentValidationRequestDto request,
            Authentication authentication) {
        try {
            String validateur = authentication.getName();
            dashboardService.validerDocument(id, request, validateur);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Document validé avec succès");
            response.put("statut", "VALIDE");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // ⚠️⚠ Debug
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la validation");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Rejette un document
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/admin/documents/{id}/rejeter")
    public ResponseEntity<Map<String, String>> rejeterDocument(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            String validateur = authentication.getName();
            String motif = request.get("motif");
            String commentaires = request.get("commentaires");

            dashboardService.rejeterDocument(id, motif, commentaires, validateur);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Document rejeté");
            response.put("statut", "REJETE");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // ⚠️⚠ Debug
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors du rejet");
            return ResponseEntity.status(500).body(error);
        }
    }

}
