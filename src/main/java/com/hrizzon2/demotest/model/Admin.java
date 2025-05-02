package com.hrizzon2.demotest.model;


import com.hrizzon2.demotest.model.enums.NiveauDroit;
import com.hrizzon2.demotest.model.enums.TypeAdmin;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("ADMINISTRATEUR")
public class Admin extends User {

    @Enumerated(EnumType.STRING)
    private TypeAdmin typeAdmin;

    @Enumerated(EnumType.STRING)
    private NiveauDroit niveauDroit;

////    serviceRattachement
}
