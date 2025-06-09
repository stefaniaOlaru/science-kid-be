package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.*;
import com.example.sciencekidbe.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/login-student")
    public ResponseEntity<AuthenticationResponse> loginStudent(
            @RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.loginStudent(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> addTeacher(@RequestBody CreateTeacherRequest request) {
        try {
            return ResponseEntity.ok( authenticationService.addTeacher(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la adăugarea profesorului.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-student")
    public ResponseEntity<?> addStudent(@RequestBody CreateStudentRequest request) {
        try {
            return ResponseEntity.ok( authenticationService.addStudent(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la adăugarea studentului."+ e.getMessage());
        }
    }

}
