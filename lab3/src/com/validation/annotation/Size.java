package com.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface Size {

    // default values
    int min() default 0;
    int max() default Integer.MAX_VALUE;

    String message() default "Za długi tekst";
}