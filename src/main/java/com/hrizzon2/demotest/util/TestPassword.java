package com.hrizzon2.demotest.util;

/**
 * Classe utilitaire pour tester l'encodage et la vérification des mots de passe.
 * <p>
 * Cette classe de test permet de vérifier le bon fonctionnement de l'encodage BCrypt
 * utilisé dans l'application. Elle génère un hash pour un mot de passe donné
 * et teste ensuite la correspondance entre le mot de passe original et son hash.
 * </p>
 *
 * <p><strong>Note :</strong> Cette classe est destinée uniquement au développement et aux tests.
 * Elle ne devrait pas être présente dans l'environnement de production.</p>
 *
 * @author Votre nom
 * @version 1.0
 * @see org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
 * @since 1.0
 */
public class TestPassword {

    /**
     * Méthode principale pour tester l'encodage BCrypt.
     * <p>
     * Cette méthode :
     * <ol>
     *   <li>Crée une instance de BCryptPasswordEncoder</li>
     *   <li>Encode le mot de passe "root"</li>
     *   <li>Affiche le hash généré</li>
     *   <li>Teste la correspondance entre le mot de passe original et le hash</li>
     * </ol>
     * </p>
     *
     * @param args arguments de ligne de commande (non utilisés)
     * @see org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder#encode(CharSequence)
     * @see org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder#matches(CharSequence, String)
     */
    public static void main(String[] args) {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String hash = encoder.encode("root");
        System.out.println("Hash généré : " + hash);
        System.out.println("Test : " + encoder.matches("root", hash));
    }
}
