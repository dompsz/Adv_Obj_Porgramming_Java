package com.validation.strategy;

import com.validation.annotation.Email;
import java.lang.reflect.Field;
import java.util.Optional;

public class EmailStrategy implements ValidationStrategy {

    // email regex
    private static final String email_regex =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @Override
    public Optional<String> validate(Field field, Object value) {
        if (field.isAnnotationPresent(Email.class)) {
            Email annotation = field.getAnnotation(Email.class);
            if (!(value instanceof String) || !((String) value).matches(email_regex)) {
                String errorInfo = String.format("Pole %s: %s", field.getName(), annotation.message());
                return Optional.of(errorInfo);
            }
        }
        return Optional.empty();
    }
}
