package com.hrizzon2.demotest;

import jakarta.validation.Validator;

class StagiaireTest {

    private Validator validator;

//    @BeforeEach
//    void setUp() {
//        ValidatorFactory factory =
//                Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//    }
//
//    @Test
//    void createValidProduit_shouldBeValid() {
//
//        Produit produitTest = new Produit();
//        produitTest.setPrix(10);
//        produitTest.setNom("Test");
//
//        Set<ConstraintViolation<Produit>> violations = validator.validate(produitTest);
//
//        assertTrue(violations.isEmpty());
//    }
//
//    @Test
//    void createProduitWithoutName_shouldNotBeValid() {
//        Produit produitTest = new Produit();
//        produitTest.setPrix(10);
//        Set<ConstraintViolation<Object>> violations = validator.validate(produitTest);
//
//        boolean notBlankViolationExist = TestUtils.constraintExist(
//                violations, "nom", "NotBlank");
//
//        assertTrue(notBlankViolationExist);
//    }
//
//    @Test
//    void createProduitWithNegativePrice_shouldNotBeValid() {
//        Produit produitTest = new Produit();
//        produitTest.setNom("test");
//        produitTest.setPrix(-10);
//
//        assertTrue(
//                TestUtils.constraintExist(
//                        validator.validate(produitTest),
//                        "prix",
//                        "DecimalMin"));
//
//    }
//
//
}