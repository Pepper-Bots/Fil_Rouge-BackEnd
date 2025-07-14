package com.hrizzon2.demotest.user.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.document.model.Document;
import com.hrizzon2.demotest.evenement.model.Evenement;
import com.hrizzon2.demotest.inscription.model.Dossier;
import com.hrizzon2.demotest.inscription.model.Inscription;
import com.hrizzon2.demotest.inscription.model.enums.StatutInscription;
import com.hrizzon2.demotest.view.AffichageDossier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Représente un stagiaire dans le système.
 *
 * <p>Cette classe hérite de {@link User} et contient des informations
 * personnelles supplémentaires spécifiques aux stagiaires, telles que
 * la date de naissance, le numéro de téléphone et l'adresse.</p>
 *
 * <p>Elle établit également des relations avec d'autres entités comme
 * {@link Evenement}, {@link Dossier} et {@link Inscription}.</p>
 *
 * <p>La valeur de discrimination dans la base de données est {@code STAGIAIRE},
 * utilisée dans la stratégie d’héritage JOINED.</p>
 *
 * @see User
 * @see Evenement
 * @see Dossier
 * @see Inscription
 */
@Getter
@Setter
@Entity
@DiscriminatorValue("STAGIAIRE")
public class Stagiaire extends User {

    @Column(name = "premiere_connexion")
    @JsonView({AffichageDossier.Stagiaire.class, AffichageDossier.Complet.class})
    private boolean premiereConnexion = true;

    /**
     * Date de naissance (date-only).
     */
    @Temporal(TemporalType.DATE)
    @JsonView({AffichageDossier.Stagiaire.class, AffichageDossier.Complet.class})
    private Date dateNaissance;

    /**
     * Numéro de téléphone du stagiaire.
     * Ce champ est obligatoire.
     */
    @JsonView({AffichageDossier.Stagiaire.class, AffichageDossier.Complet.class})
    private String phoneNumber;

    /**
     * Adresse postale du stagiaire.
     * Ce champ est obligatoire.
     */
    @JsonView({AffichageDossier.Stagiaire.class, AffichageDossier.Complet.class})
    private String adresse;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ville_id", nullable = false)
    @JsonView({AffichageDossier.Stagiaire.class, AffichageDossier.Complet.class})
    private Ville ville;

    /**
     * Liste des événements associés à ce stagiaire.
     */
    @OneToMany(mappedBy = "stagiaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView({AffichageDossier.Complet.class})
    private List<Evenement> evenements = new ArrayList<>();

    @OneToMany(mappedBy = "stagiaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    /**
     * Liste des dossiers associés à ce stagiaire.
     */
    @OneToMany(mappedBy = "stagiaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView({AffichageDossier.Complet.class})
    private List<Dossier> dossiers = new ArrayList<>();

    /**
     * Liste des inscriptions effectuées par ce stagiaire.
     */
    @OneToMany(mappedBy = "stagiaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView({AffichageDossier.Complet.class})
    private List<Inscription> inscriptions = new ArrayList<>();

    @JsonView(AffichageDossier.Admin.class)
    private String activationToken;

    /**
     * Méthode utilitaire pour obtenir le StatutInscription de la dernière inscription.
     * (@Transient pour que ce ne soit pas pris en compte en colonne JPA)
     * <p>
     * Renvoie le statut de la dernière inscription (ou null s’il n’y en a pas).
     */
    @Transient
    public StatutInscription getStatutActuelInscription() {
        if (inscriptions == null || inscriptions.isEmpty()) {
            return null;
        }
        return inscriptions.stream()
                .max(Comparator.comparing(Inscription::getDateInscription))
                .map(Inscription::getStatut)
                .orElse(null);
    }

    /**
     * Nom ou chemin relatif de la photo de profil du stagiaire.
     */
    @JsonView({AffichageDossier.Stagiaire.class, AffichageDossier.Complet.class})
    @Column(name = "photo_profil")
    private String photoProfil;

}

