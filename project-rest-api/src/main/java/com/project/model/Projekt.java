package com.project.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "projekt")
public class Projekt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "projekt_id")
    private Integer projektId;

    @NotBlank(message = "Pole nazwa nie może być puste!")
    @Size(min = 3, max = 50, message = "Nazwa musi zawierać od {min} do {max} znaków!")
    @Column(nullable = false, length = 50)
    private String nazwa;

    @Column(length = 255)
    private String opis;

    @CreatedDate
    @Column(name = "dataczas_utworzenia", nullable = false, updatable = false)
    private LocalDateTime dataCzasUtworzenia;

    @LastModifiedDate
    @Column(name = "dataczas_modyfikacji", insertable = false)
    private LocalDateTime dataCzasModyfikacji;

    private LocalDate dataOddania;

    @OneToMany(mappedBy = "projekt", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"projekt"})
    private List<Zadanie> zadania;

    @ManyToMany
    @JoinTable(
        name = "projekt_student",
        joinColumns = @JoinColumn(name = "projekt_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> studenci;

    public Projekt() { }

    public Projekt(String nazwa, String opis) {
        this.nazwa = nazwa;
        this.opis = opis;
    }

    // Gettery i settery
    public Integer getProjektId() { return projektId; }
    public void setProjektId(Integer projektId) { this.projektId = projektId; }

    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }

    public LocalDateTime getDataCzasUtworzenia() { return dataCzasUtworzenia; }
    public void setDataCzasUtworzenia(LocalDateTime dataCzasUtworzenia) { this.dataCzasUtworzenia = dataCzasUtworzenia; }

    public LocalDateTime getDataCzasModyfikacji() { return dataCzasModyfikacji; }
    public void setDataCzasModyfikacji(LocalDateTime dataCzasModyfikacji) { this.dataCzasModyfikacji = dataCzasModyfikacji; }

    public LocalDate getDataOddania() { return dataOddania; }
    public void setDataOddania(LocalDate dataOddania) { this.dataOddania = dataOddania; }

    public List<Zadanie> getZadania() { return zadania; }
    public void setZadania(List<Zadanie> zadania) { this.zadania = zadania; }

    public Set<Student> getStudenci() { return studenci; }
    public void setStudenci(Set<Student> studenci) { this.studenci = studenci; }
}
