package com.hrizzon2.demotest;

import com.hrizzon2.demotest.mock.MockConfig;
import com.hrizzon2.demotest.service.Stagiaire.StagiaireService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(MockConfig.class)
public class StagiaireTest {

    @Autowired
    private StagiaireService stagiaireService;

    @Test
    void exampleTest() {
        assert (stagiaireService != null);
        // Ajoute ici tes tests métier    }

    }
}
