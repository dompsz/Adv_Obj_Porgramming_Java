package com.validation.strategy;

import com.validation.annotation.NotEmpty;
import java.lang.reflect.Field;
import java.util.Optional;

public class NotEmptyStrategy implements ValidationStrategy {
    @Override
    public Optional<String> validate(Field field, Object value) {
        if (field.isAnnotationPresent(NotEmpty.class)) {
            NotEmpty annotation = field.getAnnotation(NotEmpty.class);
            if (!(value instanceof String) || ((String) value).trim().isEmpty()) {
                String errorInfo = String.format("Pole %s: %s", field.getName(), annotation.message());
                return Optional.of(errorInfo);
            }
        }
        return Optional.empty();
    }
}
