package com.project.model;

import jakarta.persistence.Index;
import java.util.Set;
import jakarta.persistence.Column; 
import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue; 
import jakarta.persistence.Id; 
import jakarta.persistence.Table; 
import jakarta.persistence.ManyToMany;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

@Entity   	//Indeksujemy kolumny, które są najczęściej wykorzystywane do wyszukiwania studentów 
@Table(name = "student",  
	indexes = { @Index(name = "idx_nazwisko", columnList = "nazwisko", unique = false), 
				@Index(name = "idx_nr_indeksu", columnList = "nr_indeksu", unique = true) }) 

@EntityListeners(AuditingEntityListener.class)
public class Student { 
	
	@Id 
	@GeneratedValue 
	@Column(name="student_id") 
	private Integer studentId; 
	
	@Column(length=50, nullable=false)
	@NotBlank(message = "Pole imię nie może być puste!")
	@Size(min = 2, max = 50, message = "Imię musi mieć od {min} do {max} znaków!")
	private String imie;
	
	@Column(length=100, nullable=false)
	@NotNull(message = "Pole nazwisko nie może być puste!")
	@Size(min = 2, max = 100, message = "Nazwisko musi mieć od {min} do {max} znaków!")
	private String nazwisko;
	
	@Column(name="nr_indeksu",length=20, unique=true, nullable=false)
	private String nrIndeksu;
	
	@Column(length=50, unique=true, nullable=false)
	@NotBlank(message = "Email jest wymagany!")
	@Size(max = 50, message = "Email nie może mieć więcej niż {max} znaków!")
	private String email;
	
	@Column(nullable=false)
	private Boolean stacjonarny;
	
	public Student() {} 
	
	public Student(String imie, String nazwisko, String nrIndeksu, Boolean stacjonarny) { 
		this.imie = imie; 
		this.nazwisko = nazwisko; 
		this.nrIndeksu = nrIndeksu;  
	} 
	
	public Student(String imie, String nazwisko, String nrIndeksu, String email, Boolean stacjonarny) { 
		this.imie = imie; 
		this.nazwisko = nazwisko; 
		this.nrIndeksu = nrIndeksu;  
		this.email = email; 
		this.stacjonarny = stacjonarny; 
	}

	public Integer getStudentId() {
		return studentId;
	}

	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
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

	public Boolean getStacjonarny() {
		return stacjonarny;
	}

	public void setStacjonarny(Boolean stacjonarny) {
		this.stacjonarny = stacjonarny;
	} 
	
	@ManyToMany(mappedBy = "studenci") 
	@JsonIgnoreProperties({"studenci"})
	private Set<Projekt> projekty; 
} 