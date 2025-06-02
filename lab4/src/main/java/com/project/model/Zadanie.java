package com.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "zadanie")
public class Zadanie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zadanie_id")
    private Integer zadanieId;

    @NotBlank(message = "Nazwa zadania nie może być pusta")
    private String nazwa;

    private String opis;
    private Integer kolejnosc;

    @Column(name = "dataczas_dodania")
    private LocalDateTime dataCzasDodania;

    @ManyToOne
    @JoinColumn(name = "projekt_id")
    @JsonIgnoreProperties("zadania")
    private Projekt projekt;

    public Zadanie() {}

    public Zadanie(String nazwa, String opis, Integer kolejnosc) {
        this.nazwa = nazwa;
        this.opis = opis;
        this.kolejnosc = kolejnosc;
    }

    // Gettery i settery
}
