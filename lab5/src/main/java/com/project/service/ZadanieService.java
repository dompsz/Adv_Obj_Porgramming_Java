package com.project.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.project.model.Zadanie;

public interface ZadanieService {
	Page<Zadanie> getZadania(Pageable pageable);
	List<Zadanie> getZadaniaProjektu(Integer projektId);
	Page<Zadanie> getZadaniaProjektu(Integer projektId, Pageable pageable);
	Zadanie setZadanie(Zadanie zadanie);
	void deleteZadanie(Integer zadanieId);
	Zadanie getZadanieById(Integer zadanieId);
}
