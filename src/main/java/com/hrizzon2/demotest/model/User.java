package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
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
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "nom_role", discriminatorType = DiscriminatorType.STRING)
public class User {

    @Column(name = "enabled")
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public void setActive(boolean b) {
    }

//// VOIR GESTION DES DROITS AVEC UN BOOLEEN - PAGE 415 SLIDE SPRING
//
//    public interface ValidInscription {
//    }
//
//    public interface ValidModification {
//    }

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
    @Column(nullable = false, unique = true)
    protected String email;

    /**
     * Mot de passe de l'utilisateur.
     * Ce champ ne peut pas être nul.
     */
    @Column(nullable = false)
    protected String password;

    @OneToMany(mappedBy = "destinataire", fetch = FetchType.LAZY)
    protected List<Notification> notifications;

    protected String jetonVerificationEmail;

    @Column(name = "nom_role", insertable = false, updatable = false)
    protected String nomRole;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;
}
