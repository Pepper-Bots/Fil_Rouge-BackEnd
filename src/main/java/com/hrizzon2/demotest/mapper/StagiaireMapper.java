package com.hrizzon2.demotest.mapper;

import com.hrizzon2.demotest.dto.stagiaire.StagiaireCreateDTO;
import com.hrizzon2.demotest.dto.stagiaire.StagiaireDTO;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.service.stagiaire.StagiaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


// Mapper convertit les objets (DTO ↔ entités)
@Component
public class StagiaireMapper {

    private StagiaireService stagiaireService; // Déclaration du service

    // Constructeur avec injection du service
    @Autowired
    public StagiaireMapper(@Lazy StagiaireService stagiaireService) {
        this.stagiaireService = stagiaireService;
    }


    public StagiaireDTO toDTO(Stagiaire stagiaire) {
        if (stagiaire == null) return null;

        StagiaireDTO dto = new StagiaireDTO();
        dto.setId(stagiaire.getId());
        dto.setLastName(stagiaire.getLastName());
        dto.setFirstName(stagiaire.getFirstName());
        dto.setEmail(stagiaire.getEmail());
        dto.setPhone(stagiaire.getPhone_number());

        if (stagiaire.getVille() != null) {
            dto.setVilleId(stagiaire.getVille().getIdVille());
            dto.setVilleNom(stagiaire.getVille().getNomville());
            // Que faire pour dto.setVilleId() ?
            // Si tu as besoin de l'ID de la ville dans ton DTO,
            // il faudra soit modifier la structure de ton entité Stagiaire
            // pour avoir une relation vers une entité Ville,
            // soit trouver un autre moyen de récupérer l'ID (peut-être via une convention
            // dans la String ville, ce qui n'est pas recommandé).
        }

        if (stagiaire.getDossiers() != null) {
            List<Integer> ids = stagiaire.getDossiers()
                    .stream()
                    .map(Dossier::getId)
                    .collect(Collectors.toList());
            java.util.Collections.reverse(ids);
            dto.setDossiersIds(ids);
        }

        return dto;  // Ici, on attend un objet Stagiaire et non une chaîne de caractères
    }

    public Stagiaire fromCreateDTO(StagiaireCreateDTO dto) {
        if (dto == null) return null;

        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setLastName(dto.getLastName());
        stagiaire.setFirstName(dto.getFirstName());
        stagiaire.setEmail(dto.getEmail());
        stagiaire.setPhone_number(dto.getPhone());

        // Ville et dossiers à mapper dans le service (en allant chercher les entités depuis la base)

        return stagiaire;
    }

    public Stagiaire fromDTO(StagiaireDTO dto) {
        if (dto == null) return null;

        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setId(dto.getId());
        stagiaire.setLastName(dto.getLastName());
        stagiaire.setFirstName(dto.getFirstName());
        stagiaire.setEmail(dto.getEmail());
        stagiaire.setPhone_number(dto.getPhone());

        return stagiaire;
    }

    // Imaginons que tu aies une méthode qui mappe un objet String à un Stagiaire ou vice versa
    public Stagiaire toEntity(Integer stagiaireId) {
        // Vérifie si stagiaireId est bien un objet Stagiaire, pas un String
        if (stagiaireId == null) {
            return null;
        }

        Optional<Stagiaire> stagiaireOpt = stagiaireService.findById(stagiaireId);
        return stagiaireOpt.orElseThrow(() -> new RuntimeException("Stagiaire not found"));
    }

    public Stagiaire fromUpdateDTO(Stagiaire stagiaire, StagiaireDTO dto) {
        if (dto == null) return stagiaire;

        stagiaire.setLastName(dto.getLastName());
        stagiaire.setFirstName(dto.getFirstName());
        stagiaire.setEmail(dto.getEmail());
        stagiaire.setPhone_number(dto.getPhone());

        return stagiaire;
    }
}
