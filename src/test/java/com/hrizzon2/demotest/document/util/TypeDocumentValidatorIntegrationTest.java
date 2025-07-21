package com.hrizzon2.demotest.document.util;

import com.hrizzon2.demotest.document.model.enums.TypeDocument;
import com.hrizzon2.demotest.formation.dao.FormationDao;
import com.hrizzon2.demotest.formation.model.Formation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TypeDocumentValidatorIntegrationTest {

    @Autowired
    private TypeDocumentValidator validator;

    @Autowired
    private FormationDao formationDao;

    @Test
    void shouldAllowExpectedDocumentTypesForFormation1() {
        // Formation 1 → Développement Web Front-End
        Formation formation = formationDao.findById(1)
                .orElseThrow(() -> new RuntimeException("Formation 1 non trouvée"));

        assertThat(validator.isTypeAutorise(formation, TypeDocument.JUSTIFICATIF)).isTrue();
        assertThat(validator.isTypeAutorise(formation, TypeDocument.CV)).isTrue();
        assertThat(validator.isTypeAutorise(formation, TypeDocument.LETTRE_MOTIVATION)).isTrue();
        assertThat(validator.isTypeAutorise(formation, TypeDocument.PORTFOLIO)).isTrue();

        // Exemple négatif
        assertThat(validator.isTypeAutorise(formation, TypeDocument.DIPLOME_BAC_3)).isFalse();
    }
}
