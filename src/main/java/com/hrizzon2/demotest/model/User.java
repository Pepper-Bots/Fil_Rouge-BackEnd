package com.hrizzon2.demotest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity // TODO -> ?
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
public class User {

// VOIR GESTION DES DROITS AVEC UN BOOLEEN - PAGE 415 SLIDE SPRING

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(nullable = false)
    protected String lastName;

    @Column(nullable = false)
    protected String firstName;

    @Column(nullable = false)
    protected String email;

    @Column(nullable = false)
    protected String password;
    
}
