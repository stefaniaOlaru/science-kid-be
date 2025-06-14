package com.example.sciencekidbe.service;

import com.example.sciencekidbe.config.JwtService;
import com.example.sciencekidbe.dto.*;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.enums.ERole;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.model.Teacher;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.StudentRepository;
import com.example.sciencekidbe.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
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

    public AuthenticationResponse loginStudent(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword())
        );
        var user = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole().toString())
                .teacherId(user.getStudentId())
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

    public StudentAuthenticationResponse addStudent(CreateStudentRequest createStudentRequest){
        validateEmail(createStudentRequest.getEmail());
        ClassEntity classEntity = classEntityRepository.findById(createStudentRequest.getClassId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Clasa cu ID-ul " + createStudentRequest.getClassId() + " nu a fost găsită."
                ));

        String preGeneratedStudentPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        Student student = new Student();
        student.setFullName(createStudentRequest.getFullName());
        student.setBirthdate(createStudentRequest.getBirthdate());
        student.setEmail(createStudentRequest.getEmail());
        student.setJoinDate(LocalDate.now());
        student.setPassword(passwordEncoder.encode(preGeneratedStudentPassword));
        student.setRole(ERole.ROLE_STUDENT);
        student.setClassEntity(classEntity);

        Student savedStudent = studentRepository.save(student);
        var jwtToken = jwtService.generateToken(student);

        return StudentAuthenticationResponse.builder()
                .token(jwtToken)
                .role(student.getRole().toString())
                .teacherId(savedStudent.getStudentId())
                .password(preGeneratedStudentPassword)
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
