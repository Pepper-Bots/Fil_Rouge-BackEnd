package com.hrizzon2.demotest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST permettant de récupérer dynamiquement
 * les valeurs d'un enum Java du package "model.enums".
 * <p>
 * Il expose une route GET "/api/enum/{enumName}" qui retourne
 * une liste de paires (name, label) représentant les constantes
 * du type enum spécifié.
 */
@RestController
@RequestMapping("/api/enum")
public class EnumController {

    /**
     * Retourne dynamiquement les constantes d'un enum donné.
     * Si le enum dispose d'une méthode getLabel(), son résultat
     * est utilisé comme label. Sinon, le nom brut est retourné.
     *
     * @param enumName Le nom du enum à charger (ex: StatutInscription)
     * @return Liste des constantes sous forme JSON, ou erreur HTTP
     */
    // Méthode GET pour accéder dynamiquement aux valeurs d’un enum, en passant son nom dans l’URL (ex : /api/enum/StatutInscription).
    @GetMapping("/{enumName}")
    public ResponseEntity<?> getEnumValues(@PathVariable("enumName") String enumName) {
        try {
            // Charge dynamiquement la classe enum (comme StatutInscription) depuis le package donné.
            String packageName = "com.hrizzon2.demotest.model.enums";
            Class<?> enumClass = Class.forName(packageName + "." + enumName);

            // Vérifie que la classe trouvée est bien un enum.
            if (!enumClass.isEnum()) {
                return ResponseEntity.badRequest().body("Ce type n'est pas enum.");
            }

            List<Map<String, String>> values = new ArrayList<>();

            // Boucle sur chaque constante du enum, ex : ACCEPTE, REFUSE...
            for (Object constant : enumClass.getEnumConstants()) {
                Map<String, String> entry = new HashMap<>();
                entry.put("name", constant.toString());

                // Récupère le label s'il existe
                try {
                    Method getLabelMethod = constant.getClass().getMethod("getLabel");
                    Object result = getLabelMethod.invoke(constant);
                    entry.put("label", result != null ? result.toString() : constant.toString());
                } catch (NoSuchMethodException e) {
                    entry.put("label", constant.toString());
                }
                // Si le enum a une méthode getLabel() (ex. pour avoir un label plus lisible côté front), elle est appelée. Sinon, on utilise le nom brut.
                values.add(entry);
            }
            // Retourne la liste au client front.
            return ResponseEntity.ok(values);

        } catch (ClassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Enum introuvable : " + enumName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du traitement de l'enum : " + enumName);
        }
    }
}
