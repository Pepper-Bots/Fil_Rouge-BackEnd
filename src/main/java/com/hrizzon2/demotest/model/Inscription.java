//package com.hrizzon2.demotest.model;
//
//import com.hrizzon2.demotest.model.enums.StatutInscription;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDate;
//
//@Getter
//@Setter
//@Entity
//public class Inscription {
//
//    // Cf Etat de CDA_Demo_Test
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//
//    private LocalDate dateInscription;
//    private LocalDate dateModification;
//    private LocalDate dateValidation;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "statut_inscription", nullable = false)
//    private StatutInscription statut;
//
//    @ManyToOne(optional = false)
//    private Stagiaire stagiaire;
//
//    @ManyToOne(optional = false)
//    @JoinColumn(name = "formation_id")
//    private Formation formation;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    private Dossier dossier;
//
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
