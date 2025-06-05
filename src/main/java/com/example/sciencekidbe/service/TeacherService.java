package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ClassSummaryDTO;
import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.dto.TeacherDTO;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.model.Teacher;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.TeacherRepository;
import com.example.sciencekidbe.service.interfaces.ITeacherService;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService implements ITeacherService {

    private final TeacherRepository teacherRepository;
    private final ClassEntityRepository classEntityRepository;

    public TeacherService(TeacherRepository teacherRepository, ClassEntityRepository classEntityRepository) {
        this.teacherRepository = teacherRepository;
        this.classEntityRepository = classEntityRepository;
    }

    private TeacherDTO convertToDTO(Teacher teacher){
        List<String> classNames = teacher.getClassEntities().stream()
                .map(ClassEntity::getClassName)
                .collect(Collectors.toList());

        List<Long> classIds = teacher.getClassEntities()
                .stream()
                .map(ClassEntity::getClassId)
                .collect(Collectors.toList());

        return new TeacherDTO(
                teacher.getTeacherId(),
                teacher.getFullName(),
                teacher.getEmail(),
                teacher.getSubject(),
                teacher.getPhoneNumber(),
                classNames,
                classIds
        );
    }

    public List<TeacherDTO> getAllTeacher(){
        List<Teacher> teachers = teacherRepository.findAll();

        if(teachers.isEmpty()){
            throw new IllegalArgumentException("Nu exista profesori inregistrati");
        }

        return teachers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public TeacherDTO getTeacherById(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul cu ID-ul " + teacherId + " nu există."));
        return convertToDTO(teacher);
    }

    public void deleteTeacher(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul nu există!"));

        teacherRepository.delete(teacher);
    }

    public Teacher updateTeacher(Long teacherId, TeacherDTO updatedTeacherDTO) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul nu există!"));

        validateEmail(updatedTeacherDTO.getEmail());
        validatePhoneNumber(updatedTeacherDTO.getPhoneNumber());

        List<ClassEntity> classEntities = new ArrayList<>();

        for (Long classId : updatedTeacherDTO.getClassEntityIds()) {
            ClassEntity classEntity = classEntityRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("Clasa cu ID-ul " + classId + " nu a fost găsită."));
            classEntities.add(classEntity);
        }

        teacher.setFullName(updatedTeacherDTO.getFullName());
        teacher.setEmail(updatedTeacherDTO.getEmail());
        teacher.setSubject(updatedTeacherDTO.getSubject());
        teacher.setPhoneNumber(updatedTeacherDTO.getPhoneNumber());
        teacher.setClassEntities(classEntities);

        return teacherRepository.save(teacher);
    }

    public List<TeacherDTO> getTeachersByClass(Long classId) {
        List<Teacher> teachers = teacherRepository.findByClassEntities_ClassId(classId);
        return teachers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ClassSummaryDTO> getClassesForTeacher(Long teacherId){
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul nu exista"));

        return teacher.getClassEntities().stream()
                .map(classEntity -> new ClassSummaryDTO(
                        classEntity.getClassId(),
                        classEntity.getClassName(),
                        classEntity.getGradeLevel()
                )).collect(Collectors.toList());
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

    public List<TeacherDTO> filterTeachers(String email, String fullName) {
        List<Teacher> teachers;

        if (email != null && fullName != null) {
            String loweredEmail = email.toLowerCase();
            String loweredName = fullName.toLowerCase();

            teachers = teacherRepository.findAll().stream()
                    .filter(teacher -> teacher.getFullName() != null &&
                            teacher.getFullName().toLowerCase().contains(loweredName))
                    .filter(teacher -> teacher.getEmail() != null &&
                            teacher.getEmail().toLowerCase().contains(loweredEmail))
                    .collect(Collectors.toList());

        } else if (email != null) {
            String loweredEmail = email.toLowerCase();

            teachers = teacherRepository.findAll().stream()
                    .filter(teacher -> teacher.getEmail() != null &&
                            teacher.getEmail().toLowerCase().contains(loweredEmail))
                    .collect(Collectors.toList());

        } else if (fullName != null) {
            String loweredName = fullName.toLowerCase();

            teachers = teacherRepository.findAll().stream()
                    .filter(teacher -> teacher.getFullName() != null &&
                            teacher.getFullName().toLowerCase().contains(loweredName))
                    .collect(Collectors.toList());

        } else {
            teachers = teacherRepository.findAll();
        }

        return teachers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
