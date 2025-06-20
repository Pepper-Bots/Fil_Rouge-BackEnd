package com.hrizzon2.demotest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {

    private String token;
    private boolean premiereConnexion;

    public AuthResponse(String token, boolean premiereConnexion) {
        this.token = token;
        this.premiereConnexion = premiereConnexion;
    }
}

//C’est clean, c’est pro, et ça te permet d’évoluer facilement
// (tu pourras ajouter d’autres champs à l’avenir).