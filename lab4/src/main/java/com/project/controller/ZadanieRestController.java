package com.project.controller;

import com.project.model.Zadanie;
import com.project.service.ZadanieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ZadanieRestController {

    private final ZadanieService zadanieService;

    @Autowired
    public ZadanieRestController(ZadanieService zadanieService) {
        this.zadanieService = zadanieService;
    }

    @GetMapping("/zadania/{zadanieId}")
    public ResponseEntity<Zadanie> getZadanie(@PathVariable Integer zadanieId) {
        return ResponseEntity.of(zadanieService.getZadanie(zadanieId));
    }

    @GetMapping("/projekty/{projektId}/zadania")
    public Page<Zadanie> getZadaniaProjektu(@PathVariable Integer projektId, Pageable pageable) {
        return zadanieService.getZadaniaProjektu(projektId, pageable);
    }

    @PostMapping("/zadania")
    public Zadanie createZadanie(@Valid @RequestBody Zadanie zadanie) {
        return zadanieService.setZadanie(zadanie);
    }

    @DeleteMapping("/zadania/{zadanieId}")
    public void deleteZadanie(@PathVariable Integer zadanieId) {
        zadanieService.deleteZadanie(zadanieId);
    }
}
