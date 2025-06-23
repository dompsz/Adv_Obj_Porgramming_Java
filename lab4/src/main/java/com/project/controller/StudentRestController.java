package com.project.controller;

import java.net.URI;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.project.model.Student;
import com.project.service.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api") 

@Tag(name = "Student") 
public class StudentRestController {
	private StudentService studentService; 	//serwis jest automatycznie wstrzykiwany poprzez konstruktor
	
	@Autowired
	public StudentRestController(StudentService studentService) {
		this.studentService = studentService;
	}
	
	@GetMapping("/studenci/{studentId}")
	ResponseEntity<Student> getStudent(@PathVariable("studentId") Integer studentId){	
		return ResponseEntity.of(studentService.getStudent(studentId)); 				
	} 							
	
	@PostMapping(path = "/studenci")
	ResponseEntity<Void> createProjekt(@Valid @RequestBody Student student) {		
																					
		Student createdStudent = studentService.setStudent(student); 					
		URI location = ServletUriComponentsBuilder.fromCurrentRequest() 
				.path("/{studentId}").buildAndExpand(createdStudent.getStudentId()).toUri();
		
		return ResponseEntity.created(location).build(); // zwracany jest kod odpowiedzi 201 - Created
	} 
 
	@PutMapping("/studenci/{studentId}")
	public ResponseEntity<Void> updateStudent(@Valid @RequestBody Student student,
											@PathVariable("studentId") Integer studentId) {
		return studentService.getStudent(studentId)
				.map(p -> {
					studentService.setStudent(student);
					return new ResponseEntity<Void>(HttpStatus.OK); // 200 (można też zwracać 204 - No content)
				})
				.orElseGet(() -> ResponseEntity.notFound().build()); // 404 - Not found
	}
 
	@DeleteMapping("/studenci/{studentId}")
	public ResponseEntity<Void> deleteStudent(@PathVariable("studentId") Integer studentId) {
		return studentService.getStudent(studentId).map(p -> {
			studentService.deleteStudent(studentId);
			return new ResponseEntity<Void>(HttpStatus.OK); // 200
		}).orElseGet(() -> ResponseEntity.notFound().build()); // 404 - Not found
	}
 
	@GetMapping(value = "/studenci")
	Page<Student> getStudenci(Pageable pageable) { 
		return studentService.getStudenci(pageable); 
	}
}
