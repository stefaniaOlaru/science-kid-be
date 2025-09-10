package com.example.sciencekidbe.controller;


import com.example.sciencekidbe.dto.ExerciseDTO;
import com.example.sciencekidbe.dto.ExerciseRequest;
import com.example.sciencekidbe.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/application")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService){
        this.applicationService = applicationService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    @PostMapping("/add-exercise")
    public ResponseEntity<?> addExercise(@RequestBody ExerciseRequest request) {
        try {
            return ResponseEntity.ok( applicationService.addExercise(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la adăugarea exercițiului.");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    @PutMapping("/modify-exercise/{exerciseId}")
    public ResponseEntity<?> updateExercise(@PathVariable Long exerciseId,
                                            @RequestBody ExerciseRequest request) {
        try {
            ExerciseDTO exercise = applicationService.updateExercise(exerciseId, request);
            return ResponseEntity.ok(exercise);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la modificarea exercițiului.");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    @DeleteMapping("/remove-exercise/{exerciseId}")
    public ResponseEntity<?> deleteExercise(@PathVariable Long exerciseId) {
        try {
            applicationService.deleteExercise(exerciseId);
            return ResponseEntity.ok("Exercițiul a fost șters cu succes.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la ștergerea exercițiului.");
        }
    }
}
