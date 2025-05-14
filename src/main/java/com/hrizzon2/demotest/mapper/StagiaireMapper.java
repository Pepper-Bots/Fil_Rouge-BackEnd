package com.hrizzon2.demotest.mapper;

import com.hrizzon2.demotest.dto.stagiaire.StagiaireCreateDTO;
import com.hrizzon2.demotest.dto.stagiaire.StagiaireDTO;
import com.hrizzon2.demotest.model.Dossier;
import com.hrizzon2.demotest.model.Stagiaire;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


// Mapper convertit les objets (DTO ↔ entités)
public class StagiaireMapper {


    public StagiaireDTO toDTO(Stagiaire stagiaire) {
        if (stagiaire == null) return null;

        StagiaireDTO dto = new StagiaireDTO();
        dto.setId(stagiaire.getId());
        dto.setLastName(stagiaire.getLastName());
        dto.setFirstName(stagiaire.getFirstName());
        dto.setEmail(stagiaire.getEmail());
        dto.setPhone(stagiaire.getPhone_number());

        if (stagiaire.getVille() != null) {
            dto.setVilleId(stagiaire.getVille().getId());
            dto.setVilleNom(stagiaire.getVille().getNom());
        }

        if (stagiaire.getDossiers() != null) {
            List<Long> ids = stagiaire.getDossiers()
                    .stream()
                    .map(Dossier::getId)
                    .collect(Collectors.toList());
            dto.setDossiersIds(ids);
        }

        return dto;  // Ici, on attend un objet Stagiaire et non une chaîne de caractères
    }

    public static Stagiaire fromCreateDTO(StagiaireCreateDTO dto) {
        if (dto == null) return null;

        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setLastName(dto.getLastName());
        stagiaire.setFirstName(dto.getFirstName());
        stagiaire.setEmail(dto.getEmail());
        stagiaire.setPhone_number(dto.getPhone());

        // Ville et dossiers à mapper dans le service (en allant chercher les entités depuis la base)

        return stagiaire;
    }

    public static Stagiaire fromDTO(StagiaireDTO dto) {
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
    public Stagiaire toEntity(StagiaireDTO dto) {
        // Vérifie si stagiaireId est bien un objet Stagiaire, pas un String
        // Par exemple :
//        Stagiaire stagiaire = new Stagiaire();
//        stagiaire.setId(stagiaireId);  // Ici, stagiaireId est un String, mais on attend un objet avec méthode getId()
//        return stagiaire;
        // Assure-toi que stagiaireId est utilisé pour retrouver l'entité stagiaire
        Optional<Stagiaire> stagiaireOpt = stagiaireService.findById(stagiaireId);
        return stagiaireOpt.orElseThrow(() -> new RuntimeException("Stagiaire not found"));
    }

    public static Stagiaire fromUpdateDTO(Stagiaire stagiaire, StagiaireDTO dto) {
        if (dto == null) return stagiaire;

        stagiaire.setLastName(dto.getLastName());
        stagiaire.setFirstName(dto.getFirstName());
        stagiaire.setEmail(dto.getEmail());
        stagiaire.setPhone_number(dto.getPhone());

        return stagiaire;
    }

}
