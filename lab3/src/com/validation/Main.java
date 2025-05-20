package com.validation;

import com.validation.exception.ValidationException;
import com.validation.validator.Validator;

public class Main {
    public static void main(String[] args) {
        try {
            Student student = new Student();
            student.setEmail("abc1@pbs.edu.pl");
            student.setImie("Qwerty");
            student.setNazwisko("Abcdefghij");
            student.setNrIndeksu("122404");

            Validator.validate(student);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }
}
