package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.ManualDTO;
import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            StudentDTO dto = studentService.convertToDTO(newStudent);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<?> getStudentById(@PathVariable Long studentId)
    {
        try{
            StudentDTO student = studentService.getStudentById(studentId);
            return new ResponseEntity<>(student, HttpStatus.OK);
        } catch (IllegalArgumentException e)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<StudentDTO>> getAllStudents(){
        List<StudentDTO> students = studentService.getAllStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
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
    public ResponseEntity<?> updateStudent(@RequestBody StudentDTO updatedStudent){
        try{
            studentService.updateStudent(updatedStudent);
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{studentId}/manuals")
    public ResponseEntity<List<ManualDTO>> getManualForStudent(@PathVariable Long studentId){
        try{
            Long classId = studentService.getStudentById(studentId).getClassEntityId();
            List<ManualDTO> manuals = studentService.getManualForStudent(classId);
            return new ResponseEntity<>(manuals, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<StudentDTO>> filterStudents( @RequestParam(required = false) String email,
                                                            @RequestParam(required = false) String name){
        try{
            List<StudentDTO> filteredStudents = studentService.filterStudents(email, name);
            return ResponseEntity.ok(filteredStudents);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
