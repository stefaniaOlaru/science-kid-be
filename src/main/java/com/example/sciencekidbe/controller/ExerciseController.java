package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.ExerciseDTO;
import com.example.sciencekidbe.model.Exercise;
import com.example.sciencekidbe.service.ExerciseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = "*")
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESOR')")
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(exerciseService.getExercisesByApplication(applicationId));
    }
}
