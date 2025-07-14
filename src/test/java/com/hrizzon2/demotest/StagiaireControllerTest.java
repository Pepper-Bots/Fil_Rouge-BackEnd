package com.hrizzon2.demotest;

import com.hrizzon2.demotest.user.controller.StagiaireController;

class StagiaireControllerTest {

    StagiaireController stagiaireController;

//    @BeforeEach
//    void setUp() {
//        produitController = new ProduitController(
//                new MockProduitDao(), new MockSecuriteUtils("ROLE_VENDEUR"), null
//        );
//    }
//
//    @Test
//    void callGetWithExistingProduct_shouldSend200ok() {
//        ResponseEntity<Produit> reponse = produitController.get(1);
//        Assertions.assertEquals(HttpStatus.OK, reponse.getStatusCode());
//    }
//
//    @Test
//    void callGetWithNotExistingProduct_shouldSend404notFound() {
//        ResponseEntity<Produit> reponse = produitController.get(2);
//        Assertions.assertEquals(HttpStatus.NOT_FOUND, reponse.getStatusCode());
//    }
//
//    @Test
//    void deleteExistingProductBySellerOwner_shouldSend204noContent() {
//        Vendeur fauxVendeur = new Vendeur();
//        fauxVendeur.setId(1);
//        AppUserDetails userDetails = new AppUserDetails(fauxVendeur);
//
//        ResponseEntity<Produit> reponse = produitController.delete(1, userDetails);
//        Assertions.assertEquals(HttpStatus.NO_CONTENT, reponse.getStatusCode());
//    }
//
//    @Test
//    void deleteNotExistingProduct_shouldSend404notFound() {
//        Vendeur fauxVendeur = new Vendeur();
//        fauxVendeur.setId(1);
//        AppUserDetails userDetails = new AppUserDetails(fauxVendeur);
//
//        ResponseEntity<Produit> reponse = produitController.delete(2, userDetails);
//        Assertions.assertEquals(HttpStatus.NOT_FOUND, reponse.getStatusCode());
//    }
//
//    @Test
//    void deleteExistingProductByNotSellerOwner_shouldSend403forbidden() {
//        Vendeur fauxVendeur = new Vendeur();
//        fauxVendeur.setId(2);
//        AppUserDetails userDetails = new AppUserDetails(fauxVendeur);
//
//        ResponseEntity<Produit> reponse = produitController.delete(1, userDetails);
//        Assertions.assertEquals(HttpStatus.FORBIDDEN, reponse.getStatusCode());
//    }
//
//    @Test
//    void deleteExistingProductByNotSellerOwnerButFloorWalker_shouldSend204notContent() {
//
//        produitController = new ProduitController(
//                new MockProduitDao(), new MockSecuriteUtils("ROLE_CHEF_RAYON"), null
//        );
//
//        Vendeur fauxVendeur = new Vendeur();
//        fauxVendeur.setId(2);
//        AppUserDetails userDetails = new AppUserDetails(fauxVendeur);
//
//        ResponseEntity<Produit> reponse = produitController.delete(1, userDetails);
//        Assertions.assertEquals(HttpStatus.NO_CONTENT, reponse.getStatusCode());
//    }
}
