package com.project.controller;

import jakarta.validation.Valid;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import com.project.model.Student;
import com.project.service.StudentService;

@Controller
public class StudentController {
	 private StudentService studentService;
	 
	 //@Autowired – przy jednym konstruktorze wstrzykiwanie jest zadaniem domyślnym, adnotacji nie jest potrzebna
	 public StudentController(StudentService studentService) {
		 this.studentService = studentService;
	 }
	
	 
	 @GetMapping("/studentList") 
	 public String studentList(Model model, Pageable pageable) {
		model.addAttribute("studenci", studentService.getStudenci(pageable).getContent());
		return "studentList";
	 }
	 
	 @GetMapping("/studentEdit")
	 public String studentEdit(@RequestParam(name="studentId", required = false) Integer studentId, Model model){
		 if(studentId != null) {
			 model.addAttribute("student", studentService.getStudent(studentId).get());
		 }else {
			 Student student = new Student();
			 model.addAttribute("student", student);
		 }
		 return "studentEdit";
	 }
	 
	 @PostMapping(path = "/studentEdit")
	 public String studentEditSave(@ModelAttribute @Valid Student student, BindingResult bindingResult) {
		 //parametr BindingResult powinien wystąpić zaraz za parametrem opatrzonym adnotacją @Valid
		 if (bindingResult.hasErrors()) {
			 return "studentEdit";
		 }
		 try {
			 student = studentService.setStudent(student);
		 } catch (HttpStatusCodeException e) {
			 bindingResult.rejectValue(Strings.EMPTY, String.valueOf(e.getStatusCode().value()),
					 e.getStatusCode().toString());
			 return "studentEdit";
		 }
		 return "redirect:/studentList";
	 }
	 
	 @PostMapping(params="cancel", path = "/studentEdit")
	 public String studentEditCancel() {
		 return "redirect:/studentList";
	 }
	 
	 @PostMapping(params="delete", path = "/studentEdit")
	 public String projektEditDelete(@ModelAttribute Student student) {
		 studentService.deleteStudent(student.getStudentId());
		 return "redirect:/studentList";
	 }
}
