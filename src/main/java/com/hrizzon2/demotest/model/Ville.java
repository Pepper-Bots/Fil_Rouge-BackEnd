package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Entité représentant une ville dans la base de données.
 *
 * <p>Cette entité contient les informations suivantes :</p>
 * <ul>
 *   <li>Identifiant unique de la ville (idVille)</li>
 *   <li>Code postal associé à la ville</li>
 *   <li>Nom de la ville</li>
 *   <li>Relation ManyToOne avec {@link Region}</li>
 *   <li>Relation OneToMany avec la liste des {@link Stagiaire} résidant dans cette ville</li>
 * </ul>
 *
 * <p>La table correspondante dans la base est nommée "ville".</p>
 */
@Getter
@Setter
@Entity
@Table(name = "ville")
public class Ville {

    /**
     * Identifiant unique auto-généré de la ville.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idVille;

    /**
     * Code postal de la ville.
     */
    private String codePostal;

    /**
     * Nom de la ville.
     */
    @Column(unique = false)
    private String nomVille;

    /**
     * Région à laquelle appartient la ville.
     */
    @ManyToOne
    @JoinColumn(name = "id_region")
    private Region region;

    /**
     * Liste des stagiaires rattachés à cette ville.
     */
    @OneToMany(mappedBy = "ville")
    private List<Stagiaire> stagiaires;


}
