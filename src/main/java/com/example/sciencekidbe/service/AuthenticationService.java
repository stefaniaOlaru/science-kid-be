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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        ClassEntity classEntity = classEntityRepository.findById(createStudentRequest.getClassEntityId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Clasa cu ID-ul " + createStudentRequest.getClassEntityId() + " nu a fost găsită."
                ));

        if (calculateAge(createStudentRequest.getBirthdate()) < 7 || calculateAge(createStudentRequest.getBirthdate()) > 12) {
            throw new IllegalArgumentException("Varsta elevului trebuie sa fie intre 7 si 12 ani.");
        }

        if (!isValidEmailFormat(createStudentRequest.getEmail())) {
            throw new IllegalArgumentException("Email-ul nu corespunde formatului.");
        }

        if (classEntity.getStudents().size() >= 30) {
            throw new IllegalArgumentException("The class " + classEntity.getClassName() + " is already full.");
        }
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

        writeStudentCredentialsToCsv(classEntity.getClassName(), savedStudent.getFullName(), preGeneratedStudentPassword);

        return StudentAuthenticationResponse.builder()
                .token(jwtToken)
                .role(student.getRole().toString())
                .teacherId(savedStudent.getStudentId())
                .password(preGeneratedStudentPassword)
                .build();
    }

    private void writeStudentCredentialsToCsv(String className, String fullName, String password) {
        String directory = "C:/Users/stanbydrew/Desktop/science-kid-be/src/main";
        String fileName = directory + "/" + className + ".csv"; // full path to file

        File file = new File(fileName);

        try (FileWriter writer = new FileWriter(file, true)) { // true = append mode
            // add header if file is new/empty
            if (file.length() == 0) {
                writer.append("Name,Password\n");
            }

            writer.append(fullName)
                    .append(",")
                    .append(password)
                    .append("\n");
        } catch (IOException e) {
            throw new RuntimeException("Eroare la scrierea în fișierul CSV pentru clasă: " + className, e);
        }
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

    private int calculateAge(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    private boolean isValidEmailFormat(String email) {
        String emailRegex = "^[a-zA-Z]+\\.[A-Z]+\\.[a-zA-Z]+@elev\\.ro$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

}
