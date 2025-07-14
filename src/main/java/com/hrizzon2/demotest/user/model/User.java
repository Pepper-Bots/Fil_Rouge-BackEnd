package com.hrizzon2.demotest.user.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.hrizzon2.demotest.notification.model.Notification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
@DiscriminatorColumn(name = "nom_role", discriminatorType = DiscriminatorType.STRING)
public class User {


//// VOIR GESTION DES DROITS AVEC UN BOOLEEN - PAGE 415 SLIDE SPRING


    /**
     * Identifiant unique de l'utilisateur.
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;


    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

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
    @NotNull(message = "L'email ne peut pas être null.")
    @NotBlank(message = "L'email ne peut pas être vide.")

    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
            message = "L'adresse email n'est pas valide.")

    @Column(nullable = false, unique = true)
    protected String email;

    /**
     * Mot de passe de l'utilisateur.
     * Ce champ ne peut pas être nul.
     */
    @NotBlank
    @Column(nullable = false)
    @JsonView()
    protected String password;

    @OneToMany(mappedBy = "destinataire", fetch = FetchType.LAZY)
    protected List<Notification> notifications;

    @Column(name = "jeton_verification_email")
    protected String jetonVerificationEmail;

    @Column(name = "nom_role", insertable = false, updatable = false)
    protected String nomRole;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

}
