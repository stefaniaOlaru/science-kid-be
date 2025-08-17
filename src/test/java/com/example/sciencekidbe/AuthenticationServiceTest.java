package com.example.sciencekidbe;

import com.example.sciencekidbe.config.JwtService;
import com.example.sciencekidbe.dto.*;
import com.example.sciencekidbe.enums.ERole;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.model.Teacher;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.StudentRepository;
import com.example.sciencekidbe.repository.TeacherRepository;
import com.example.sciencekidbe.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ClassEntityRepository classEntityRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void login_shouldReturnToken_whenTeacherExists() {
        SigninRequest request = new SigninRequest();
        request.setEmail("teacher@example.com");
        request.setPassword("password");

        Teacher teacher = new Teacher();
        teacher.setTeacherId(1L);
        teacher.setRole(ERole.ROLE_PROFESOR);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(teacherRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(teacher));
        when(jwtService.generateToken(any(Teacher.class))).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("ROLE_PROFESOR", response.getRole());
        assertEquals(1L, response.getTeacherId());
    }

    @Test
    void login_shouldThrowException_whenTeacherNotFound() {
        SigninRequest request = new SigninRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(teacherRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.login(request));
    }

    @Test
    void loginStudent_shouldReturnToken_whenStudentExists() {
        SigninRequest request = new SigninRequest();
        request.setEmail("student@example.com");
        request.setPassword("password");

        Student student = new Student();
        student.setStudentId(2L);
        student.setRole(ERole.ROLE_STUDENT);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(studentRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(student));
        when(jwtService.generateToken(any(Student.class))).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.loginStudent(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("ROLE_STUDENT", response.getRole());
        assertEquals(2L, response.getTeacherId());
    }

    @Test
    void addTeacher_shouldSaveTeacherAndReturnResponse() {
        CreateTeacherRequest request = new CreateTeacherRequest();
        request.setFullName("John Doe");
        request.setEmail("teacher@example.com");
        request.setPhoneNumber("0123456789");
        request.setPassword("pass");
        request.setClassEntityIds(List.of(1L));

        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassId(1L);

        when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(i -> (Teacher) i.getArgument(0));
        when(jwtService.generateToken(any(Teacher.class))).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.addTeacher(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("ROLE_PROFESOR", response.getRole());
    }

    @Test
    void addStudent_shouldSaveStudentAndReturnResponse() {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setFullName("Jane Student");
        request.setEmail("student@example.com");
        request.setBirthdate(LocalDate.of(2010, 1, 1));
        request.setClassId(1L);

        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassId(1L);

        when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> (Student) i.getArgument(0));
        when(jwtService.generateToken(any(Student.class))).thenReturn("jwt-token");

        StudentAuthenticationResponse response = authenticationService.addStudent(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("ROLE_STUDENT", response.getRole());
        assertNotNull(response.getPassword());
    }

    @Test
    void validateEmail_shouldThrowException_whenInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.addTeacher(
                    new CreateTeacherRequest() {{
                        setEmail("invalid-email");
                        setPhoneNumber("0123456789");
                        setFullName("Name");
                        setPassword("pass");
                        setClassEntityIds(List.of(1L));
                    }}
            );
        });
    }

    @Test
    void validatePhoneNumber_shouldThrowException_whenInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.addTeacher(
                    new CreateTeacherRequest() {{
                        setEmail("teacher@example.com");
                        setPhoneNumber("12345");
                        setFullName("Name");
                        setPassword("pass");
                        setClassEntityIds(List.of(1L));
                    }}
            );
        });
    }
}

