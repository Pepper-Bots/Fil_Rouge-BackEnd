package com.hrizzon2.demotest.document.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentValidationRequestDto {
    private Long documentId;
    private String statut; // 'VALIDE' ou 'REJETE'
    private String motif;
    private String commentaires;
}