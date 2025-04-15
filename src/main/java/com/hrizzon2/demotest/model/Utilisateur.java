//package com.hrizzon2.demotest.model;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//@Entity // TODO -> ?
//@Inheritance(strategy = InheritanceType.JOINED)
//@DiscriminatorColumn(name = "user_type")
//public class Utilisateur {
//
/// / VOIR GESTION DES DROITS AVEC UN BOOLEEN - PAGE 415 SLIDE SPRING
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    // TODO -> ?
//    protected Integer id;
//
//    @Column(nullable = false)
//    protected String nom;
//
//    @Column(nullable = false)
//    protected String prenom;
//
//    @Column(nullable = false)
//    protected String email;
//
//    @Column(nullable = false)
//    protected String password;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    protected Role role;
//
//}
