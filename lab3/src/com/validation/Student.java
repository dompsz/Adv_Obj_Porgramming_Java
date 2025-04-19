package com.validation;

import com.validation.annotation.Email;
import com.validation.annotation.NotNull;
import com.validation.annotation.NrIndeksu;
import com.validation.annotation.Size;
import com.validation.annotation.NotEmpty;

public class Student {
    @NotNull(message = "Nie wpisano imienia")
    @Size(min = 3, max = 50,
            message = "Imię musi mieć {min} - {max} znaków")
    private String imie;

    @NotNull(message = "Nie wpisano nazwiska")
    @Size(min = 2, max = 50,
            message = "Nazwisko musi mieć {min} - {max} znaków")
    private String nazwisko;

    @NotNull(message = "Nie wpisano numeru indeksu")
    @NrIndeksu(message = "Niepoprawny numer indeksu")
    private String nrIndeksu;

    @NotEmpty(message = "Nie wpisano adresu e-mail")
    @Email(message = "Niepoprawny adres e-mail")
    private String email;
    public Student(){
    }

    public Student(String imie, String nazwisko, String nrIndeksu, String email) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nrIndeksu = nrIndeksu;
        this.email = email;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getNrIndeksu() {
        return nrIndeksu;
    }

    public void setNrIndeksu(String nrIndeksu) {
        this.nrIndeksu = nrIndeksu;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}