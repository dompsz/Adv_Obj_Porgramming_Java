package com.project.model; 

import java.time.LocalDateTime;
import jakarta.persistence.Column; 
import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue;  
import jakarta.persistence.Id; 
import jakarta.persistence.Table; 
import jakarta.persistence.JoinColumn; 
import jakarta.persistence.ManyToOne; 

//import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

@Entity 
@Table(name="zadanie") 
@EntityListeners(AuditingEntityListener.class)
public class Zadanie { 
	@Id 
	@GeneratedValue 
	@Column(name="zadanie_id") 
	private Integer zadanieId; 
	
	@ManyToOne 
	@JoinColumn(name="projekt_id")
	@JsonIgnoreProperties({"zadania"})
	private Projekt projekt;
	
	@Column(nullable = false, length=50)
	@NotBlank(message = "Nazwa zadania jest wymagana!")
	@Size(min = 3, max = 50, message = "Nazwa zadania musi mieć od {min} do {max} znaków!")
	private String nazwa;
	
	@Column
	private Integer kolejnosc;
	
	@Column(length=1000)
	@Size(max = 1000, message = "Opis nie może mieć więcej niż {max} znaków!")
	private String opis;
	
	@Column(nullable = true, updatable = false)
	private LocalDateTime dataCzasDodania;

	public Integer getZadanieId() {
		return zadanieId;
	}

	public void setZadanieId(Integer zadanieId) {
		this.zadanieId = zadanieId;
	}

	public Projekt getProjekt() {
		return projekt;
	}

	public void setProjekt(Projekt projekt) {
		this.projekt = projekt;
	}

	public String getNazwa() {
		return nazwa;
	}

	public void setNazwa(String nazwa) {
		this.nazwa = nazwa;
	}

	public Integer getKolejnosc() {
		return kolejnosc;
	}

	public void setKolejnosc(Integer kolejnosc) {
		this.kolejnosc = kolejnosc;
	}

	public String getOpis() {
		return opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}

	public LocalDateTime getDataCzasDodania() {
		return dataCzasDodania;
	}
	
	public void setDataCzasDodania(LocalDateTime dataCzasDodania) {
		this.dataCzasDodania = dataCzasDodania;
	}

	public Zadanie() {
		super();
	}

	public Zadanie(String nazwa, Integer kolejnosc, String opis) {
		super();
		this.nazwa = nazwa;
		this.kolejnosc = kolejnosc;
		this.opis = opis;
	}
} 