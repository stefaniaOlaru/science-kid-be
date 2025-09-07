package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.StudentAnswerDTO;
import com.example.sciencekidbe.dto.StudentAnswerRequest;
import com.example.sciencekidbe.model.StudentAnswer;
import com.example.sciencekidbe.service.StudentAnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @PostMapping("/submit/batch")
    public ResponseEntity<?> submitAnswers(@RequestBody List<StudentAnswerRequest> answerRequests) {
        try {
            List<StudentAnswerDTO> savedAnswers = studentAnswerService.saveStudentAnswers(answerRequests);
            return ResponseEntity.ok(savedAnswers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/already-solved/student/{studentId}/application/{applicationId}")
    public ResponseEntity<?> isApplicationAlreadySolved(
            @PathVariable Long studentId,
            @PathVariable Long applicationId
    ) {
        try {
            boolean isSolved = studentAnswerService.isApplicationSolvedByStudent(studentId, applicationId);
            return ResponseEntity.ok(isSolved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESOR')")
    @GetMapping("/exercise/{exerciseId}/application/{applicationId}/answers/class/{classEntityId}")
    public ResponseEntity<?> getAnswersByExerciseAndApplication(
            @PathVariable Long exerciseId,
            @PathVariable Long applicationId,
            @PathVariable Long classEntityId) {
        try {
            return ResponseEntity.ok(studentAnswerService.getAnswersByExerciseAndApplication(exerciseId, applicationId, classEntityId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESOR')")
    @GetMapping("/exercise/{exerciseId}/application/{applicationId}/class/{classId}/stats")
    public ResponseEntity<?> getAnswerStats(
            @PathVariable Long exerciseId,
            @PathVariable Long applicationId,
            @PathVariable Long classId) {
        try {
            return ResponseEntity.ok(studentAnswerService.getAnswerStats(exerciseId, applicationId, classId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESOR')")
    @GetMapping("/application/{applicationId}/class/{classId}/incorrect-stats")
    public ResponseEntity<?> getIncorrectStats(
            @PathVariable Long applicationId,
            @PathVariable Long classId) {
        return ResponseEntity.ok(studentAnswerService.getIncorrectCountsByExercise(applicationId, classId));
    }


}
