package com.project.model; 

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

//import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;
 
@Entity 
@Table(name="projekt", 
	indexes = { @Index(name = "idx_nazwa", columnList = "nazwa", unique = false), 
				@Index(name = "idx_dataczas_utworzenia", columnList = "dataczas_utworzenia", unique = true) })

@EntityListeners(AuditingEntityListener.class)
public class Projekt { 
	 @Id 
	 @GeneratedValue 
	 @Column(name="projekt_id") //tylko jeżeli nazwa kolumny w bazie danych ma być inna od nazwy zmiennej 
	 private Integer projektId; 
	 
	 @NotBlank(message = "Pole nazwa nie może być puste!")
	 @Size(min = 3, max = 50, message = "Nazwa musi zawierać od {min} do {max} znaków!")
	 @Column(nullable = false, length = 50) 
	 private String nazwa; 
	 
	 @Column(length = 1000)
	 @Size(max = 1000, message = "Opis nie może mieć więcej niż {max} znaków!")
	 private String opis;
	 
	 @CreatedDate 
	 @Column(name = "dataczas_utworzenia", nullable = false, updatable = false) 
	 private LocalDateTime dataCzasUtworzenia; 
	 
	 @LastModifiedDate
	 @Column(name="dataczas_modyfikacji", insertable=false)
	 private LocalDateTime dataCzasModyfikacji;
	 
	@Column()
	private LocalDate dataOddania;
	 
	 public LocalDateTime getDataCzasUtworzenia() {
		return dataCzasUtworzenia;
	}

	public void setDataCzasUtworzenia(LocalDateTime dataCzasUtworzenia) {
		this.dataCzasUtworzenia = dataCzasUtworzenia;
	}

	public LocalDateTime getDataCzasModyfikacji() {
		return dataCzasModyfikacji;
	}

	public void setDataCzasModyfikacji(LocalDateTime dataCzasModyfikacji) {
		this.dataCzasModyfikacji = dataCzasModyfikacji;
	}

	public LocalDate getDataOddania() {
		return dataOddania;
	}

	public void setDataOddania(LocalDate dataOddania) {
		this.dataOddania = dataOddania;
	}
	 
	 public Projekt() {
		super();
	}

	public Projekt(String nazwa, String opis) {
		super();
		this.nazwa = nazwa;
		this.opis = opis;
	}
	 
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

	@OneToMany(mappedBy = "projekt") 
	@JsonIgnoreProperties({"projekt"})
	private List<Zadanie> zadania;

	public List<Zadanie> getZadania() {
		return zadania;
	}

	public void setZadania(List<Zadanie> zadania) {
		this.zadania = zadania;
	} 
	
	@ManyToMany  
	@JoinTable(name = "projekt_student", 
		joinColumns = {@JoinColumn(name="projekt_id")},  
	    inverseJoinColumns = {@JoinColumn(name="student_id")})  
	private Set<Student> studenci;

	public Set<Student> getStudenci() {
		return studenci;
	}

	public void setStudenci(Set<Student> studenci) {
		this.studenci = studenci;
	}  

	public Projekt(Integer projektId, String nazwa, String opis, LocalDateTime dataCzasUtworzenia, LocalDate dataOddania){ 
		super();
		this.projektId = projektId;
		this.nazwa = nazwa;
		this.opis = opis;
		this.dataCzasUtworzenia = dataCzasUtworzenia;
		this.dataOddania = dataOddania;
	}
}