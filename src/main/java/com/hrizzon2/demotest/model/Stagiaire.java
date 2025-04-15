//package com.hrizzon2.demotest.model;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.Date;
//
//@Getter
//@Setter
//@Entity
//@DiscriminatorValue("STAGIAIRE")
//public class Stagiaire extends Utilisateur {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @Column(unique = true, nullable = false)
//    private Date dateNaissance;
//
//    @Column(unique = true, nullable = false)
//    private Number numeroDeTelephone;
//
//    @Column(unique = true, nullable = false)
//    private String adresse;
//
//
//}
