package com.hrizzon2.demotest.model;


import com.hrizzon2.demotest.model.enums.NiveauDroit;
import com.hrizzon2.demotest.model.enums.TypeAdmin;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

/**
 * Représente un administrateur du système.
 *
 * <p>Cette classe hérite de {@link User} et ajoute des propriétés spécifiques
 * aux administrateurs comme leur type et leur niveau de droit.</p>
 *
 * <p>La valeur de discrimination dans la base de données est {@code ADMINISTRATEUR},
 * ce qui permet de distinguer les instances d'Admin dans la stratégie
 * d’héritage JOINED.</p>
 *
 * @see User
 * @see TypeAdmin
 * @see NiveauDroit
 */
@Entity
@Getter
@Setter
@DiscriminatorValue("ADMINISTRATEUR")
public class Admin extends User {

    /**
     * Type d'administrateur (ex. : SUPER_ADMIN, RH, TECHNIQUE, etc.).
     * Stocké sous forme de chaîne de caractères.
     */
    @Enumerated(EnumType.STRING)
    private TypeAdmin typeAdmin;

    /**
     * Niveau de droit associé à cet administrateur (ex. : LECTURE, ECRITURE, COMPLET).
     * Stocké sous forme de chaîne de caractères.
     */
    @Enumerated(EnumType.STRING)
    private NiveauDroit niveauDroit;


}
