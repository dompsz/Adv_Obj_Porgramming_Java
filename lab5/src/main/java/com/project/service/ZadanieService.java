package com.project.service;

import com.project.model.Zadanie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ZadanieService {
    Optional<Zadanie> getZadanie(Integer id);
    Zadanie setZadanie(Zadanie zadanie);
    void deleteZadanie(Integer id);
    Page<Zadanie> getZadaniaProjektu(Integer projektId, Pageable pageable);
}
