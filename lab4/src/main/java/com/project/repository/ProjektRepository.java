package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.model.Projekt;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjektRepository extends JpaRepository<Projekt, Integer> {
	Page<Projekt> findByNazwaContainingIgnoreCase(String nazwa, Pageable pageable);
	List<Projekt> findByNazwaContainingIgnoreCase(String nazwa);
	 
	// Metoda findByNazwaContainingIgnoreCase definiuje zapytanie
	// SELECT p FROM Projekt p WHERE upper(p.nazwa) LIKE upper(%:nazwa%)
}