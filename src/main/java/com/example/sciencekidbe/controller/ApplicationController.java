package com.example.sciencekidbe.controller;


import com.example.sciencekidbe.dto.ExerciseDTO;
import com.example.sciencekidbe.dto.ExerciseRequest;
import com.example.sciencekidbe.model.Application;
import com.example.sciencekidbe.model.Exercise;
import com.example.sciencekidbe.model.Observation;
import com.example.sciencekidbe.service.ApplicationService;
import com.example.sciencekidbe.service.ObservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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

    @PreAuthorize("hasRole('ADMIN','PROFESOR')")
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

    @PreAuthorize("hasRole('ADMIN','PROFESOR')")
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

    @PreAuthorize("hasRole('ADMIN','PROFESOR')")
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
