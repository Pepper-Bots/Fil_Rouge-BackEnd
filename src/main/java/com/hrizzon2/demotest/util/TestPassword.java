package com.hrizzon2.demotest.util;

public class TestPassword {
    public static void main(String[] args) {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String hash = encoder.encode("root");
        System.out.println("Hash généré : " + hash);
        System.out.println("Test : " + encoder.matches("root", hash));
    }
}
