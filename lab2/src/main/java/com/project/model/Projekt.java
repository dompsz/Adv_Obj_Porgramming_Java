package com.project.model;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Projekt {
    private Integer projektId;
    private String nazwa;
    private String opis;
    private LocalDateTime dataCzasUtworzenia;
    private LocalDate dataOddania;

    // Bezparametrowy konstruktor
    public Projekt() {
    }

    // Konstruktor z wszystkimi polami
    public Projekt(Integer projektId, String nazwa, String opis, LocalDateTime dataCzasUtworzenia, LocalDate dataOddania) {
        this.projektId = projektId;
        this.nazwa = nazwa;
        this.opis = opis;
        this.dataCzasUtworzenia = dataCzasUtworzenia;
        this.dataOddania = dataOddania;
    }

    // Konstruktor bez pola projektId
    public Projekt(String nazwa, String opis, LocalDateTime dataCzasUtworzenia, LocalDate dataOddania) {
        this.nazwa = nazwa;
        this.opis = opis;
        this.dataCzasUtworzenia = dataCzasUtworzenia;
        this.dataOddania = dataOddania;
    }

    // Gettery i settery
    public Integer getProjektId() {
        return projektId;
    }

    public void setProjektId(Integer projektId) {
        this.projektId = projektId;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public LocalDateTime getDataCzasUtworzenia() {
        return dataCzasUtworzenia;
    }

    public void setDataCzasUtworzenia(LocalDateTime dataCzasUtworzenia) {
        this.dataCzasUtworzenia = dataCzasUtworzenia;
    }

    public LocalDate getDataOddania() {
        return dataOddania;
    }

    public void setDataOddania(LocalDate dataOddania) {
        this.dataOddania = dataOddania;
    }
}
