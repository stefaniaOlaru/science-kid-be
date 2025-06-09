package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.ChangePassword;
import com.example.sciencekidbe.dto.ClassSummaryDTO;
import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.dto.TeacherDTO;
import com.example.sciencekidbe.model.Teacher;
import com.example.sciencekidbe.service.TeacherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<TeacherDTO>> getAllTeachers(){
        try{
            List<TeacherDTO> teachers = teacherService.getAllTeacher();
            return new  ResponseEntity<>(teachers, HttpStatus.OK);
        } catch(IllegalArgumentException e){
            return new  ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESOR')")
    @GetMapping("/{teacherId}")
    public ResponseEntity<?> getTeacherById(@PathVariable Long teacherId)
    {
        try{
            TeacherDTO teacherDTO = teacherService.getTeacherById(teacherId);
            return new ResponseEntity<>(teacherDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody ChangePassword dto) {
        try {
            teacherService.changePassword(id, dto.getOldPassword(), dto.getNewPassword());
            return ResponseEntity.ok("Parola a fost schimbată cu succes");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{teacherId}")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long teacherId) {
        try {
            teacherService.deleteTeacher(teacherId);
            return ResponseEntity.ok("Profesor șters cu succes.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la ștergerea profesorului.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{teacherId}")
    public ResponseEntity<?> updateTeacher(@PathVariable Long teacherId, @RequestBody TeacherDTO teacherDTO) {
        try {
            teacherService.updateTeacher(teacherId, teacherDTO);
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la actualizarea profesorului.");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESOR')")
    @GetMapping("/class/{classId}")
    public ResponseEntity<?> getTeachersByClass(@PathVariable Long classId) {
        try {
            List<TeacherDTO> teachers = teacherService.getTeachersByClass(classId);
            if (teachers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nu există profesori pentru această clasă.");
            }
            return ResponseEntity.ok(teachers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la obținerea profesorilor.");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESOR')")
    @GetMapping("/{teacherId}/classes")
    public ResponseEntity<?> getClassesForTeacher(@PathVariable Long teacherId) {
        try {
            List<ClassSummaryDTO> classes = teacherService.getClassesForTeacher(teacherId);
            return ResponseEntity.ok(classes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Eroare la preluarea claselor profesorului.");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESOR')")
    @GetMapping("/filter")
    public ResponseEntity<List<TeacherDTO>> filterStudents(@RequestParam(required = false) String email,
                                                           @RequestParam(required = false) String name){
        try{
            List<TeacherDTO> filteredTechers = teacherService.filterTeachers(email, name);
            return ResponseEntity.ok(filteredTechers);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
