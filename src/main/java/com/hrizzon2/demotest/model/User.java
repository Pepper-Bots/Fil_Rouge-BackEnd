package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Représente un utilisateur générique dans le système.
 *
 * <p>Cette classe est conçue pour être héritée par d'autres types d'utilisateurs
 * (comme un administrateur, un client, etc.), en utilisant une stratégie d'héritage
 * par jointure avec une colonne de discrimination {@code user_type}.</p>
 *
 * <p>Les annotations Lombok {@code @Getter} et {@code @Setter} génèrent automatiquement
 * les accesseurs et mutateurs pour tous les champs.</p>
 *
 * <p>Champs obligatoires : {@code lastName}, {@code firstName}, {@code email}, {@code password}</p>
 *
 * <p><strong>Note :</strong> Il est mentionné qu’une gestion des droits via un booléen pourrait
 * être implémentée (cf. page 415 des slides Spring).</p>
 *
 * @author ...
 */
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
public class User {

// VOIR GESTION DES DROITS AVEC UN BOOLEEN - PAGE 415 SLIDE SPRING

    /**
     * Identifiant unique de l'utilisateur.
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    /**
     * Nom de famille de l'utilisateur.
     * Ce champ ne peut pas être nul.
     */
    @Column(nullable = false)
    protected String lastName;

    /**
     * Prénom de l'utilisateur.
     * Ce champ ne peut pas être nul.
     */
    @Column(nullable = false)
    protected String firstName;

    /**
     * Adresse email de l'utilisateur.
     * Ce champ ne peut pas être nul.
     */
    @Column(nullable = false)
    protected String email;

    /**
     * Mot de passe de l'utilisateur.
     * Ce champ ne peut pas être nul.
     */
    @Column(nullable = false)
    protected String password;

}
