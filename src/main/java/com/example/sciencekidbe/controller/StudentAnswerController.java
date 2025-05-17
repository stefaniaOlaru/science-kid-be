package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.StudentAnswerDTO;
import com.example.sciencekidbe.dto.StudentAnswerRequest;
import com.example.sciencekidbe.model.StudentAnswer;
import com.example.sciencekidbe.service.StudentAnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/student-answers")
@CrossOrigin(origins = "*")
public class StudentAnswerController {
    private final StudentAnswerService studentAnswerService;

    public StudentAnswerController(StudentAnswerService studentAnswerService) {
        this.studentAnswerService = studentAnswerService;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitAnswer(@RequestBody StudentAnswerRequest studentAnswerRequest) {
        try {
            LocalDateTime answeredAt = LocalDateTime.now();
            StudentAnswer savedAnswer = studentAnswerService.saveStudentAnswer(studentAnswerRequest.getStudentId(),
                    studentAnswerRequest.getExerciseId(), studentAnswerRequest.getGivenAnswer(), answeredAt);
            return ResponseEntity.ok(savedAnswer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/exercise/{exerciseId}/application/{applicationId}/answers")
    public ResponseEntity<?> getAnswersByExerciseAndApplication(
            @PathVariable Long exerciseId,
            @PathVariable Long applicationId) {
        try {
            return ResponseEntity.ok(studentAnswerService.getAnswersByExerciseAndApplication(exerciseId, applicationId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
