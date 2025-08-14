package com.hrizzon2.demotest.formation.dto;

import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FormationAvecStatutDto {
    private Integer id;
    private String nom;
    private String description;
    private int pourcentageCompletion;

    // Documents requis pour cette formation
    private List<TypeDocument> documentsRequis;

    // Documents déjà uploadés par le stagiaire
    private List<TypeDocument> documentsUploades;

    // statut global du dossier
    private String statutDossier; // INCOMPLET, COMPLET, EN_VALIDATION, VALIDE

    private int nombreDocumentsRequis;
    private int nombreDocumentsUploades;
    private int nombreDocumentsManquants;

    // Méthodes utilitaires (optionnelles)
    public int getNombreDocumentsRequis() {
        return documentsRequis != null ? documentsRequis.size() : 0;
    }

    public int getNombreDocumentsUploades() {
        return documentsUploades != null ? documentsUploades.size() : 0;
    }

    public int getNombreDocumentsManquants() {
        return getNombreDocumentsRequis() - getNombreDocumentsUploades();
    }
}