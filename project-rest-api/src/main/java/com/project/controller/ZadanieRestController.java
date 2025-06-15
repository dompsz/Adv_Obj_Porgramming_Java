package com.project.controller;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
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
    public ResponseEntity<Zadanie> getZadanie(@PathVariable Integer zadanieId) {
        return ResponseEntity.of(zadanieService.getZadanie(zadanieId));
    }

    @GetMapping(value = "/zadania", params = "projektId")
    public Page<Zadanie> getZadaniaProjektuPage(@RequestParam Integer projektId, Pageable pageable) {
        return zadanieService.getZadaniaProjektu(projektId, pageable);
    }

    @GetMapping(value = "/zadania/list", params = "projektId")
    public List<Zadanie> getZadaniaProjektuList(@RequestParam Integer projektId) {
        return zadanieService.getZadaniaProjektu(projektId);
    }

    @PostMapping("/zadania")
    public ResponseEntity<Void> createZadanie(@Valid @RequestBody Zadanie zadanie) {
        Zadanie created = zadanieService.setZadanie(zadanie);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getZadanieId())
            .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/zadania/{zadanieId}")
    public ResponseEntity<Object> updateZadanie(@Valid @RequestBody Zadanie zadanie,
                                              @PathVariable Integer zadanieId) {
        return zadanieService.getZadanie(zadanieId)
            .map(z -> {
                zadanie.setZadanieId(zadanieId);
                zadanieService.setZadanie(zadanie);
                return ResponseEntity.ok().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/zadania/{zadanieId}")
    public ResponseEntity<Object> deleteZadanie(@PathVariable Integer zadanieId) {
        return zadanieService.getZadanie(zadanieId)
            .map(z -> {
                zadanieService.deleteZadanie(zadanieId);
                return ResponseEntity.ok().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
