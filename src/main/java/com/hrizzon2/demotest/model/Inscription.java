package com.hrizzon2.demotest.model;

import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.model.enums.StatutInscription;
import com.hrizzon2.demotest.user.model.Stagiaire;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@Entity
@Table(name = "inscription")
public class Inscription {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscription")
    private Integer id;

    /**
     * Date à laquelle l’inscription a été lancée (par un admin ou par le stagiaire).
     */
    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    /**
     * Date de la dernière modification de cet enregistrement (par ex. changement de statut).
     */
    @Column(name = "date_modification")
    private LocalDate dateModification;

    /**
     * Date à laquelle un admin a validé l’inscription (ou à laquelle
     * le statut a basculé en VALIDEE). Peut être null tant que l’inscription n’est pas validée.
     */
    @Column(name = "date_validation")
    private LocalDate dateValidation;

    /**
     * Le statut actuel de l’inscription (EN_ATTENTE, VALIDEE, REFUSEE, ...).
     * On stocke la valeur de l’énum sous forme de String en base.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_inscription", nullable = false, length = 20)
    private StatutInscription statut;

    /**
     * Lien vers le stagiaire qui fait cette inscription.
     * (Plusieurs inscriptions peuvent exister pour un même stagiaire.)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stagiaire_id", nullable = false)
    private Stagiaire stagiaire;

    /**
     * Lien vers la formation à laquelle on est en train de s’inscrire.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    /**
     * Le dossier créé (ou à compléter) pour cette inscription.
     * Relation 1 : 1, en cascade pour que la création d’une inscription
     * crée automatiquement un dossier si besoin.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_id", unique = true)
    private Dossier dossier;

}


//
//    // TODO  Inscription.java (entité JPA) |
//    //  - Le champ private Dossier dossier; dans Inscription peut entraîner une circularité
//    //  (un Dossier référence une Inscription et l’inverse).
//    //  Or, dans le business (cf. règles métiers), on doit plutôt :
//    //  « Lorsqu’un stagiaire s’inscrit, un dossier est créé automatiquement ».
//    //  Créer cette relation bidirectionnelle peut rendre les insersions JPA plus complexes.
//    //  - Pas d’annotation pour statut : le champ private StatutInscription statut;
//    //  n’a pas @Enumerated ni @Column.
//    //  JPA ne saura pas comment persister cette énumération.
//
//    // TODO 1. Si la relation “1 Inscription → 1 Dossier” est réellement nécessaire,
//    //  ajoutez @OneToOne(mappedBy="inscription", cascade=CascadeType.ALL) dans Dossier
//    //  et @OneToOne @JoinColumn(name="dossier_id") dans Inscription.
//    //  Autrement, supprimez la référence Dossier de Inscription et gérez la création de dossier via le service
//    //  (DossierService.createFromInscription(inscription)).
//
//    // => Est ce qu'il ne vaut pas mieux conserver cette classe pour gérer
//    // la date d'inscription (quand dossier complet) et/ou date de pré-inscription ?
//}

// D’après votre MCD, une Inscription représente exactement ce moment où un stagiaire se déclare pour une formation (première ou suivante).
// Les champs principaux de cette table sont :

//id_inscription (clé primaire, auto-générée)
//
//    date_inscription, date_creation, date_modification, éventuellement date_validation
//
//    statut_inscription (enum)
//
//    Clé étrangère vers Stagiaire (pour savoir qui s’inscrit)
//
//    Clé étrangère vers Formation (à quelle formation s’inscrit-il ?)
//
//    Clé étrangère vers Dossier (le dossier que l’on crée/assigne à cette inscription)
