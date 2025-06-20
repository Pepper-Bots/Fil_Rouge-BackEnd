import jakarta.validation.ConstraintViolation;

import java.util.Set;

public class TestUtils {

    public static boolean constraintExist(Set<ConstraintViolation<Object>> violations, String fieldName, String constraintName) {
        return violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals(fieldName))
                .map(v -> v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName())
                .anyMatch(s -> s.equals(constraintName));
    }

}