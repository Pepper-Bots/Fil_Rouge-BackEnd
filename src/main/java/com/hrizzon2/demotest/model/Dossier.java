package com.hrizzon2.demotest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Dossier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    // ENUM : COMPLET / INCOMPLET / VALIDE / EN ATTENTE
    @Enumerated(EnumType.STRING)
    private StatutDossier statut = StatutDossier.EN_ATTENTE_DE_VALIDATION;

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.ALL) // TODO -> à vérifier
    private List<Document> documents;

    // Date de dernière mise à jour automatique (optionnel)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;

    // Lien vers le stagiaire concerné
    // Un dossier ne peut pas être créé sans stagiaire
    @NotNull
    @ManyToOne
    @JoinColumn(name = "stagiaire_id", nullable = false)
    private Stagiaire stagiaire;

    @PrePersist
    @PreUpdate
    public void updateTimeStamp() {
        this.lastUpdated = LocalDateTime.now();
        // Dernière mise à jour du dossier
        // TODO : @Annotations ??
    }
}
