package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.AuthenticationResponse;
import com.example.sciencekidbe.dto.CreateTeacherRequest;
import com.example.sciencekidbe.dto.SigninRequest;
import com.example.sciencekidbe.dto.TeacherDTO;
import com.example.sciencekidbe.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

}
