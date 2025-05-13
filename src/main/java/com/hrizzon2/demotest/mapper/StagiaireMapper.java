package com.hrizzon2.demotest.mapper;

import com.hrizzon2.demotest.dto.stagiaire.StagiaireCreateDTO;
import com.hrizzon2.demotest.dto.stagiaire.StagiaireDTO;
import com.hrizzon2.demotest.model.Stagiaire;

// Mapper convertit les objets (DTO ↔ entités)

public class StagiaireMapper {

    public static StagiaireDTO toDTO(Stagiaire stagiaire) {
        if (stagiaire == null) return null;

        StagiaireDTO dto = new StagiaireDTO();
        dto.setId(stagiaire.getId());
        dto.setLastName(stagiaire.getLastName());
        dto.setFirstName(stagiaire.getFirstName());
        dto.setEmail(stagiaire.getEmail());
        dto.setPhone(stagiaire.getPhone_number());
        return dto;
    }

    public static Stagiaire fromCreateDTO(StagiaireCreateDTO dto) {
        if (dto == null) return null;

        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setLastName(dto.getLastName());
        stagiaire.setFirstName(dto.getFirstName());
        stagiaire.setEmail(dto.getEmail());
        stagiaire.setPhone_number(dto.getPhone());
        return stagiaire;
    }

}
