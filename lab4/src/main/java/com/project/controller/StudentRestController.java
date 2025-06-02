package com.project.controller;

import com.project.model.Student;
import com.project.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
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

    @GetMapping("/studenci")
    public Page<Student> getStudenci(Pageable pageable) {
        return studentService.getStudenci(pageable);
    }

    @PostMapping("/studenci")
    public Student createStudent(@Valid @RequestBody Student student) {
        return studentService.setStudent(student);
    }

    @DeleteMapping("/studenci/{studentId}")
    public void deleteStudent(@PathVariable Integer studentId) {
        studentService.deleteStudent(studentId);
    }
}
