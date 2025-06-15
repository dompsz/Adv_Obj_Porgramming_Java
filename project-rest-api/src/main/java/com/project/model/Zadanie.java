package com.project.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "zadanie")
public class Zadanie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zadanie_id")
    private Integer zadanieId;

    @NotBlank(message = "Pole nazwa nie może być puste!")
    @Column(nullable = false, length = 100)
    private String nazwa;

    @Column(length = 255)
    private String opis;

    @NotNull(message = "Kolejność jest wymagana")
    private Integer kolejnosc;

    @CreatedDate
    @Column(name = "data_czas_dodania", nullable = false, updatable = false)
    private LocalDateTime dataCzasDodania;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projekt_id", nullable = false)
    @JsonIgnoreProperties({"zadania", "studenci"})
    private Projekt projekt;

    public Zadanie() { }

    public Zadanie(String nazwa, String opis, Integer kolejnosc) {
        this.nazwa = nazwa;
        this.opis = opis;
        this.kolejnosc = kolejnosc;
    }

    // Gettery i settery
    public Integer getZadanieId() { return zadanieId; }
    public void setZadanieId(Integer zadanieId) { this.zadanieId = zadanieId; }

    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }

    public Integer getKolejnosc() { return kolejnosc; }
    public void setKolejnosc(Integer kolejnosc) { this.kolejnosc = kolejnosc; }

    public LocalDateTime getDataCzasDodania() { return dataCzasDodania; }
    public void setDataCzasDodania(LocalDateTime dataCzasDodania) { this.dataCzasDodania = dataCzasDodania; }

    public Projekt getProjekt() { return projekt; }
    public void setProjekt(Projekt projekt) { this.projekt = projekt; }
}
