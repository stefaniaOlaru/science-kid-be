package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.ManualDTO;
import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.model.Manual;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addStudent(@RequestBody StudentDTO studentDTO){
        try{
            Student newStudent = studentService.addStudent(studentDTO);

            return new ResponseEntity<>(newStudent,HttpStatus.CREATED);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<StudentDTO>> getAllStudents(){
        try{
            List<StudentDTO> students = studentService.getAllStudent();
            return new ResponseEntity<>(students, HttpStatus.OK);
        }catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId){
        try{
            studentService.deleteStudent(studentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Student> updateStudent(@RequestBody StudentDTO updatedStudent){
        try{
            Student student = studentService.updateStudent(updatedStudent);
            return new ResponseEntity<>(student, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/manuals")
    public ResponseEntity<List<ManualDTO>> getManualForStudent(@RequestParam String studentEmail){
        try{
            String className = studentService.findClassNameByEmail(studentEmail);
            List<ManualDTO> manuals = studentService.getManualForStudent(className);
            return new ResponseEntity<>(manuals, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
        }

    }
}
