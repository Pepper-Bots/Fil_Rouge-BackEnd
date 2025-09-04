package com.hrizzon2.demotest.regression;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

class DocumentUploadSansMongoDBTest {

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    void shouldUplaodDocumentWithoutMongoDB() {

        // Test que l'upload marche même si MongoDB est down
        // Votre app doit continuer à fonctionner

    }
}
