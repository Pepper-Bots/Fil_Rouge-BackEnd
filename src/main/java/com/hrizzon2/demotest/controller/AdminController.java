package com.hrizzon2.demotest.controller;

public class AdminController {

    // TODO - Gestion des admins, droits, statistiques
    //  Créer
    //  Pour centraliser gestion administrateur
}


// Controller sans service :
//C’est possible mais déconseillé, surtout dans des applications à logique métier.
// Le controller ne doit pas contenir de logique métier, juste orchestrer les appels.
// Si tu n’as pas de logique métier, un controller peut appeler directement le DAO, mais ce n’est pas une bonne pratique
// (difficile à maintenir, tester, sécuriser).