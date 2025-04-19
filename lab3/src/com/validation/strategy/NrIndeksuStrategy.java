package com.validation.strategy;

import com.validation.annotation.NrIndeksu;
import java.lang.reflect.Field;
import java.util.Optional;

public class NrIndeksuStrategy implements ValidationStrategy {
    private static final String id_regex = "\\d{6}";

    @Override
    public Optional<String> validate(Field field, Object value) {
        if (field.isAnnotationPresent(NrIndeksu.class)) {
            NrIndeksu annotation = field.getAnnotation(NrIndeksu.class);
            if (!(value instanceof String) || !((String) value).matches(id_regex)) {
                String errorInfo = String.format("Pole %s: %s", field.getName(), annotation.message());
                return Optional.of(errorInfo);
            }
        }
        return Optional.empty();
    }
}
