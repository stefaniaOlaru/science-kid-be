package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ManualDTO;
import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.Manual;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.StudentRepository;
import com.example.sciencekidbe.service.interfaces.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
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

    @Autowired
    public StudentService(StudentRepository studentRepository, ClassEntityRepository classEntityRepository){
        this.studentRepository = studentRepository;
        this.classEntityRepository = classEntityRepository;
    }

    @Override
    public Student addStudent(StudentDTO studentDTO) {
        if (studentRepository.findByEmail(studentDTO.getEmail()) != null ){
            throw new IllegalArgumentException("Email-ul este deja utilizat.");
        }

        if(calculateAge(studentDTO.getBirthdate()) < 7 || calculateAge(studentDTO.getBirthdate()) > 12){
            throw new IllegalArgumentException("Varsta elevului trebuie sa fie intre 7 i 12 ani.");
        }

        if (!isValidEmailFormat(studentDTO.getEmail())){
            throw new IllegalArgumentException("Email-ul nu corespunde formatului.");
        }

        ClassEntity classEntity = classEntityRepository.findByClassName(studentDTO.getClassName());

        if (classEntity == null){
            throw new IllegalArgumentException("Clasa specificata nu exista.");
        }

        if (classEntity.getStudents().size() >= 30){
            throw new IllegalArgumentException("The class " + classEntity.getClassName() + " is already full.");
        }

        Student student = new Student();

        student.setStudentId(student.getStudentId());
        student.setBirthdate(studentDTO.getBirthdate());
        student.setEmail(studentDTO.getEmail());
        student.setClassEntity(classEntity);
        student.setFullName(studentDTO.getFullName());
        student.setJoinDate(LocalDate.now());

        return studentRepository.save(student);
    }

    private StudentDTO convertToDTO(Student student){
        return new StudentDTO(
                student.getStudentId(),
                student.getFullName(),
                student.getEmail(),
                student.getBirthdate(),
                student.getJoinDate(),
                student.getClassEntity().getClassName()
        );
    }

    public String findClassNameByEmail(String email){
        Student student = studentRepository.findByEmail(email);

        if(student == null){
            throw new IllegalArgumentException("Studentul cu acest email nu a fost gasit");
        }

        ClassEntity classEntity = student.getClassEntity();
        if(classEntity == null){
            throw new IllegalArgumentException("Studentul nu apartine unei clase");
        }

        return classEntity.getClassName();
    }


    @Override
    public List<StudentDTO> getAllStudent() {
        List<Student> students = studentRepository.findAll();

        if (students.isEmpty()){
            throw new IllegalArgumentException("Nu exista elevi inregistrati");
        }
        return students.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ManualDTO> getManualForStudent(String className){
        ClassEntity classEntity = classEntityRepository.findByClassName(className);

        if(classEntity == null || classEntity.getManuals() == null){
            throw new IllegalArgumentException("Nu s-au gasit manuale pentru clasa specificata");
        }

        return classEntity.getManuals().stream()
                .map(manual -> new ManualDTO(
                        manual.getManualId(),
                        manual.getManualName(),
                        manual.getPublisher(),
                        manual.getClassEntities().stream()
                                .map(ClassEntity::getClassName)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Student updateStudent(StudentDTO updatedStudent) {
        Student student = studentRepository.findById(updatedStudent.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Studentul cu id-ul specificat nu exista"));

        ClassEntity classEntity = classEntityRepository.findByClassName(updatedStudent.getClassName());

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

    public int calculateAge(LocalDate birthdate){
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    private boolean isValidEmailFormat(String email){
        String emailRegex = "^[a-zA-Z]+\\.[A-Z]+\\.[a-zA-Z]+@elev\\.ro$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

}
