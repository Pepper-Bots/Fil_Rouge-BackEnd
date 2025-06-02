package com.hrizzon2.demotest.exception;
// Gestion personnalisée des erreurs

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// va gérer toutes les erreurs REST automatiques proprement (404, 403).

/**
 * Classe de gestion globale des exceptions pour les contrôleurs REST.
 * <p>
 * Permet de gérer et de personnaliser les réponses pour certaines exceptions courantes,
 * telles que les erreurs 404 (entité non trouvée) et 403 (accès interdit).
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     * Gère les exceptions de type {@link EntityNotFoundException}.
     * <p>
     * Retourne une réponse HTTP 404 (NOT FOUND) avec le message de l'exception.
     * </p>
     *
     * @param ex l'exception levée lorsque l'entité demandée n'est pas trouvée
     * @return une réponse contenant le code 404 et le message d'erreur
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Gère les exceptions de type {@link SecurityException}.
     * <p>
     * Retourne une réponse HTTP 403 (FORBIDDEN) avec le message de l'exception.
     * </p>
     *
     * @param ex l'exception levée lorsqu'une action interdite est tentée
     * @return une réponse contenant le code 403 et le message d'erreur
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleNotFound(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}
