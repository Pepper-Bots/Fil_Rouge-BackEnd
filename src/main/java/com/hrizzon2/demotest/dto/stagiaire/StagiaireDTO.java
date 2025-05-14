package com.hrizzon2.demotest.dto.stagiaire;

// Pour l'affichage

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StagiaireDTO {

    private int id;
    private String lastName;
    private String firstName;
    private String email;
    private Number phone;
    private Long villeId;
    private String villeNom;
    private List<Long> dossiersIds;

}


// L'annotation @SuppressWarnings("unused") est une manière de dire au compilateur :
// "Hé, je sais que cette chose n'est pas utilisée pour le moment, mais c'est intentionnel.
// S'il te plaît, ignore cet avertissement."