package com.project.model;

import java.util.Set;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(
    name = "student",
    indexes = {
        @Index(name = "idx_nazwisko", columnList = "nazwisko"),
        @Index(name = "idx_nr_indeksu", columnList = "nr_indeksu", unique = true)
    }
)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @NotBlank(message = "Imię nie może być puste")
    private String imie;

    @NotBlank(message = "Nazwisko nie może być puste")
    private String nazwisko;

    @NotBlank(message = "Numer indeksu jest wymagany")
    @Column(name = "nr_indeksu", unique = true, nullable = false)
    private String nrIndeksu;

    @Email(message = "Nieprawidłowy format e-mail")
    private String email;

    @Column(nullable = false)
    private Boolean stacjonarny;

    @ManyToMany(mappedBy = "studenci")
    private Set<Projekt> projekty;

    public Student() { }

    public Student(String imie, String nazwisko, String nrIndeksu, Boolean stacjonarny) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nrIndeksu = nrIndeksu;
        this.stacjonarny = stacjonarny;
    }

    public Student(String imie, String nazwisko, String nrIndeksu, String email, Boolean stacjonarny) {
        this(imie, nazwisko, nrIndeksu, stacjonarny);
        this.email = email;
    }

    // Gettery i settery
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }

    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }

    public String getNrIndeksu() { return nrIndeksu; }
    public void setNrIndeksu(String nrIndeksu) { this.nrIndeksu = nrIndeksu; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getStacjonarny() { return stacjonarny; }
    public void setStacjonarny(Boolean stacjonarny) { this.stacjonarny = stacjonarny; }

    public Set<Projekt> getProjekty() { return projekty; }
    public void setProjekty(Set<Projekt> projekty) { this.projekty = projekty; }
}
