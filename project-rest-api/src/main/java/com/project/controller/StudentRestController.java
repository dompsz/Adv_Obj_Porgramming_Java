package com.project.controller;

import java.net.URI;
import java.util.Optional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.project.model.Student;
import com.project.service.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Student")
public class StudentRestController {

    private final StudentService studentService;

    @Autowired
    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/studenci/{studentId}")
    public ResponseEntity<Student> getStudent(@PathVariable Integer studentId) {
        return ResponseEntity.of(studentService.getStudent(studentId));
    }

    @GetMapping(value = "/studenci", params = "nrIndeksu")
    public Page<Student> getByNrIndeksu(@RequestParam String nrIndeksu, Pageable pageable) {
        return studentService.searchByNrIndeksu(nrIndeksu, pageable);
    }

    @GetMapping(value = "/studenci", params = "nazwisko")
    public Page<Student> getByNazwisko(@RequestParam String nazwisko, Pageable pageable) {
        return studentService.searchByNazwisko(nazwisko, pageable);
    }

    @GetMapping("/studenci")
    public Page<Student> getStudenci(Pageable pageable) {
        return studentService.getStudenci(pageable);
    }

    @PostMapping("/studenci")
    public ResponseEntity<Void> createStudent(@Valid @RequestBody Student student) {
        Student created = studentService.setStudent(student);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getStudentId())
            .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/studenci/{studentId}")
    public ResponseEntity<Object> updateStudent(@Valid @RequestBody Student student,
                                              @PathVariable Integer studentId) {
        return studentService.getStudent(studentId)
            .map(s -> {
                student.setStudentId(studentId);
                studentService.setStudent(student);
                return ResponseEntity.ok().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/studenci/{studentId}")
    public ResponseEntity<Object> deleteStudent(@PathVariable Integer studentId) {
        return studentService.getStudent(studentId)
            .map(s -> {
                studentService.deleteStudent(studentId);
                return ResponseEntity.ok().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
