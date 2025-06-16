package com.project.service;

import com.project.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface StudentService {
    Optional<Student> getStudent(Integer id);
    Student setStudent(Student student);
    void deleteStudent(Integer id);
    Page<Student> getStudenci(Pageable pageable);
}
