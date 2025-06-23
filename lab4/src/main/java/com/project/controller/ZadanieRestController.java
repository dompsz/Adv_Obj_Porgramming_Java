package com.project.controller;

import java.net.URI;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.project.model.Zadanie;
import com.project.service.ZadanieService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Zadanie")
public class ZadanieRestController {

    private final ZadanieService zadanieService;

    @Autowired
    public ZadanieRestController(ZadanieService zadanieService) {
        this.zadanieService = zadanieService;
    }

    @GetMapping("/zadania/{zadanieId}")
    public ResponseEntity<Zadanie> getZadanie(@PathVariable("zadanieId") Integer zadanieId) {
        Zadanie zadanie = zadanieService.getZadanieById(zadanieId);
        return zadanie != null
                ? ResponseEntity.ok(zadanie)
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/zadania")
    public ResponseEntity<Void> createZadanie(@Valid @RequestBody Zadanie zadanie) {
        Zadanie createdZadanie = zadanieService.setZadanie(zadanie);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{zadanieId}")
                .buildAndExpand(createdZadanie.getZadanieId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/zadania/{zadanieId}")
    public ResponseEntity<Void> updateZadanie(@Valid @RequestBody Zadanie zadanie,
                                              @PathVariable("zadanieId") Integer zadanieId) {
        Zadanie existing = zadanieService.getZadanieById(zadanieId);
        if (existing != null) {
            zadanie.setZadanieId(zadanieId); // upewniamy się, że ID się zgadza
            zadanieService.setZadanie(zadanie);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/zadania/{zadanieId}")
    public ResponseEntity<Void> deleteZadanie(@PathVariable("zadanieId") Integer zadanieId) {
        Zadanie existing = zadanieService.getZadanieById(zadanieId);
        if (existing != null) {
            zadanieService.deleteZadanie(zadanieId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/zadania")
    public Page<Zadanie> getZadania(Pageable pageable) {
        return zadanieService.getZadania(pageable);
    }

    @GetMapping("/projekty/{projektId}/zadania")
    public Page<Zadanie> getZadaniaProjektu(@PathVariable Integer projektId, Pageable pageable) {
        return zadanieService.getZadaniaProjektu(projektId, pageable);
    }
}
