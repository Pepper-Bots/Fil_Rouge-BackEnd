package com.hrizzon2.demotest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationEmailDto {

    protected String token;
    protected boolean consentementUtilisationDonnees;
}
