package com.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.OneToMany;

@Entity
@Table(name="projekt") //TODO Indeksować kolumny, które są najczęściej wykorzystywane do wyszukiwania projektów
public class Projekt {
@Id
@GeneratedValue
@Column(name="projekt_id") //tylko jeżeli nazwa kolumny w bazie danych ma być inna od nazwy zmiennej
private Integer projektId;
@Column(nullable = false, length = 50)
private String nazwa;
/*TODO Uzupełnij kod o zmienne reprezentujące pozostałe pola tabeli projekt (patrz rys. 3.1),
. następnie wygeneruj dla nich tzw. akcesory i mutatory (Source -> Generate Getters and Setters),
. ponadto dodaj pusty konstruktor oraz konstruktor ze zmiennymi nazwa i opis.
*/
@CreatedDate
@Column(name = "dataczas_utworzenia", nullable = false, updatable = false)
private LocalDateTime createdDate;
@LastModifiedDate
@Column(name = "dataczas_modyfikacji", insertable = false)
private LocalDateTime lastModifiedDate;

@OneToMany(mappedBy = "projekt")
private List<Zadanie> zadania;
}
