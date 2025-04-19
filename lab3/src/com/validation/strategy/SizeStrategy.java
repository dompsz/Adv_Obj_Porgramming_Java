package com.validation.strategy;

import com.validation.annotation.Size;
import java.lang.reflect.Field;
import java.util.Optional;

public class SizeStrategy implements ValidationStrategy {
    @Override
    public Optional<String> validate(Field field, Object value) {
        if (field.isAnnotationPresent(Size.class)) {
            Size annotation = field.getAnnotation(Size.class);
            if (!(value instanceof String)) {
                String errorInfo = String.format("Pole %s: %s", field.getName(), annotation.message());
                return Optional.of(errorInfo);
            }

            int length = ((String) value).length();
            if (length < annotation.min() || length > annotation.max()) {
                String errorInfo = String.format("Pole %s: %s (długość: %d, poprawma dlugość: %d-%d)",
                        field.getName(), annotation.message(), length, annotation.min(), annotation.max());
                return Optional.of(errorInfo);
            }
        }
        return Optional.empty();
    }
}
