package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ManualDTO;
import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.enums.ERole;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.model.Teacher;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.StudentRepository;
import com.example.sciencekidbe.service.interfaces.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class StudentService implements IStudentService {

    private final StudentRepository studentRepository;
    private final ClassEntityRepository classEntityRepository;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StudentService(StudentRepository studentRepository, ClassEntityRepository classEntityRepository, FileService fileService, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.classEntityRepository = classEntityRepository;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Student addStudent(StudentDTO studentDTO) {
        if (studentRepository.findByEmail(studentDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email-ul este deja utilizat.");
        }

        if (calculateAge(studentDTO.getBirthdate()) < 7 || calculateAge(studentDTO.getBirthdate()) > 12) {
            throw new IllegalArgumentException("Varsta elevului trebuie sa fie intre 7 si 12 ani.");
        }

        if (!isValidEmailFormat(studentDTO.getEmail())) {
            throw new IllegalArgumentException("Email-ul nu corespunde formatului.");
        }

        ClassEntity classEntity = classEntityRepository.findById(studentDTO.getClassEntityId()).orElse(null);

        if (classEntity == null) {
            throw new IllegalArgumentException("Clasa specificata nu exista.");
        }

        if (classEntity.getStudents().size() >= 30) {
            throw new IllegalArgumentException("The class " + classEntity.getClassName() + " is already full.");
        }

        Student student = new Student();

        student.setStudentId(student.getStudentId());
        student.setBirthdate(studentDTO.getBirthdate());
        student.setEmail(studentDTO.getEmail());
        student.setClassEntity(classEntity);
        student.setFullName(studentDTO.getFullName());
        student.setJoinDate(studentDTO.getJoinDate());
        student.setRole(ERole.ROLE_STUDENT);

        return studentRepository.save(student);
    }

    @Override
    public StudentDTO getStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);

        if (student == null) {
            throw new IllegalArgumentException("Studentul cu acest id nu a fost gasit");
        }

        StudentDTO dto = convertToDTO(student);

        String originalPath = student.getImagePath();
        if (originalPath != null && !originalPath.isEmpty()) {
            String urlWithSas = fileService.generateUrlWithSasToken(originalPath);
            dto.setImagePath(urlWithSas);
        }

        return dto;
    }

    public StudentDTO convertToDTO(Student student) {
        return new StudentDTO(
                student.getStudentId(),
                student.getFullName(),
                student.getEmail(),
                student.getBirthdate(),
                student.getJoinDate(),
                student.getClassEntity().getClassName(),
                student.getClassEntity().getClassId(),
                student.getImagePath()
        );
    }


    @Override
    public List<StudentDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();

        return students.stream()
                .map(student -> {
                    StudentDTO dto = convertToDTO(student);

                    String originalPath = student.getImagePath();
                    if (originalPath != null && !originalPath.isEmpty()) {
                        String urlWithSas = fileService.generateUrlWithSasToken(originalPath);
                        dto.setImagePath(urlWithSas);
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ManualDTO> getManualForStudent(Long classId) {
        ClassEntity classEntity = classEntityRepository.findById(classId).orElse(null);

        if (classEntity == null || classEntity.getManuals() == null) {
            throw new IllegalArgumentException("Nu s-au gasit manuale pentru clasa specificata");
        }

        return classEntity.getManuals().stream()
                .map(manual -> {
                    List<Long> ids = manual.getClassEntities().stream()
                            .map(ClassEntity::getClassId)
                            .collect(Collectors.toList());

                    List<String> names = manual.getClassEntities().stream()
                            .map(ClassEntity::getClassName)
                            .collect(Collectors.toList());

                    ManualDTO dto = new ManualDTO();
                    dto.setManualId(manual.getManualId());
                    dto.setManualName(manual.getManualName());
                    dto.setPublisher(manual.getPublisher());
                    dto.setClassEntityIds(ids);
                    dto.setClassNames(names);

                    return dto;
                })
                .collect(Collectors.toList());

    }

    @Override
    public Student updateStudent(StudentDTO updatedStudent) {
        Student student = studentRepository.findById(updatedStudent.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Studentul cu id-ul specificat nu exista"));

        ClassEntity classEntity = classEntityRepository.findById(updatedStudent.getClassEntityId()).orElse(null);

        if (classEntity == null) {
            throw new IllegalArgumentException("Clasa cu id-ul specificat nu exista");
        }

        student.setBirthdate(updatedStudent.getBirthdate());
        student.setEmail(updatedStudent.getEmail());
        student.setFullName(updatedStudent.getFullName());
        student.setClassEntity(classEntity);

        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Studentul cu id-ul specificat nu exista"));

        studentRepository.delete(student);
    }

    public int calculateAge(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    private boolean isValidEmailFormat(String email) {
        String emailRegex = "^[a-zA-Z]+\\.[A-Z]+\\.[a-zA-Z]+@elev\\.ro$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public List<StudentDTO> filterStudents(String email, String fullName) {
        List<Student> students;

        if (email != null && fullName != null) {
            String loweredEmail = email.toLowerCase();
            String loweredName = fullName.toLowerCase();

            students = studentRepository.findAll().stream()
                    .filter(student -> student.getFullName() != null &&
                            student.getFullName().toLowerCase().contains(loweredName))
                    .filter(student -> student.getEmail() != null &&
                            student.getEmail().toLowerCase().contains(loweredEmail))
                    .collect(Collectors.toList());

        } else if (email != null) {
            String loweredEmail = email.toLowerCase();

            students = studentRepository.findAll().stream()
                    .filter(student -> student.getEmail() != null &&
                            student.getEmail().toLowerCase().contains(loweredEmail))
                    .collect(Collectors.toList());

        } else if (fullName != null) {
            String loweredName = fullName.toLowerCase();

            students = studentRepository.findAll().stream()
                    .filter(student -> student.getFullName() != null &&
                            student.getFullName().toLowerCase().contains(loweredName))
                    .collect(Collectors.toList());

        } else {
            students = studentRepository.findAll();
        }

        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void changePassword(Long studentId, String oldPassword, String newPassword) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Studentul nu a fost găsit"));

        if (!passwordEncoder.matches(oldPassword, student.getPassword())) {
            throw new IllegalArgumentException("Parola veche este incorectă");
        }

        student.setPassword(passwordEncoder.encode(newPassword));
        studentRepository.save(student);
    }
}
