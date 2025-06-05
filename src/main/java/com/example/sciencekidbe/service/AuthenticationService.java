package com.example.sciencekidbe.service;

import com.example.sciencekidbe.config.JwtService;
import com.example.sciencekidbe.dto.AuthenticationResponse;
import com.example.sciencekidbe.dto.CreateTeacherRequest;
import com.example.sciencekidbe.dto.SigninRequest;
import com.example.sciencekidbe.dto.TeacherDTO;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.ERole;
import com.example.sciencekidbe.model.Teacher;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final TeacherRepository teacherRepository;
    private final ClassEntityRepository classEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse login(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword())
        );
        var user = teacherRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole().toString())
                .teacherId(user.getTeacherId())
                .build();
    }

    public AuthenticationResponse addTeacher(CreateTeacherRequest request) {
        List<ClassEntity> classEntities = new ArrayList<>();
        validateEmail(request.getEmail());
        validatePhoneNumber(request.getPhoneNumber());

        for (Long classId : request.getClassEntityIds()) {
            ClassEntity classEntity = classEntityRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("Clasa cu ID-ul " + classId + " nu a fost găsită."));
            classEntities.add(classEntity);
        }

        Teacher teacher = new Teacher();
        teacher.setFullName(request.getFullName());
        teacher.setEmail(request.getEmail());
        teacher.setSubject(request.getSubject());
        teacher.setPhoneNumber(request.getPhoneNumber());
        teacher.setPassword(passwordEncoder.encode(request.getPassword()));
        teacher.setRole(ERole.ROLE_PROFESOR);
        teacher.setClassEntities(classEntities);

        Teacher savedTeacher = teacherRepository.save(teacher);
        var jwtToken = jwtService.generateToken(teacher);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(teacher.getRole().toString())
                .teacherId(savedTeacher.getTeacherId())
                .build();

    }

    private void validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (email == null || !email.matches(emailRegex)) {
            throw new IllegalArgumentException("Emailul introdus nu este valid.");
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        String phoneRegex = "^0\\d{9}$"; // începe cu 0 și urmat de 9 cifre
        if (phoneNumber == null || !phoneNumber.matches(phoneRegex)) {
            throw new IllegalArgumentException("Numărul de telefon trebuie să aibă exact 10 cifre și să înceapă cu 0.");
        }
    }

}
