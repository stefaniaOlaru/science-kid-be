package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ClassSummaryDTO;
import com.example.sciencekidbe.dto.TeacherDTO;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.Teacher;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.TeacherRepository;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

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

        return new TeacherDTO(
                teacher.getTeacherId(),
                teacher.getFullName(),
                teacher.getEmail(),
                teacher.getSubject(),
                teacher.getPhoneNumber(),
                classNames
        );
    }

    public List<TeacherDTO> getAllTeacher(){
        List<Teacher> teachers = teacherRepository.findAll();

        if(teachers.isEmpty()){
            throw new IllegalArgumentException("Nu exista profesori inregistrati");
        }

        return teachers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Teacher addTeacher(TeacherDTO teacherDTO) {
      List<ClassEntity> classEntities = new ArrayList<>();

      for(String className: teacherDTO.getClassNames()){
          ClassEntity classEntity = classEntityRepository.findByClassName(className);

          if (classEntity != null) {
              classEntities.add(classEntity);
          } else {
              throw new IllegalArgumentException("Clasa " + className + " nu a fost găsită.");
          }
      }

        Teacher teacher = new Teacher();
        teacher.setFullName(teacherDTO.getFullName());
        teacher.setEmail(teacherDTO.getEmail());
        teacher.setSubject(teacherDTO.getSubject());
        teacher.setPhoneNumber(teacherDTO.getPhoneNumber());
        teacher.setClassEntities(classEntities);

        return teacherRepository.save(teacher);
    }

    public void deleteTeacher(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul nu există!"));

        teacherRepository.delete(teacher);
    }

    public Teacher updateTeacher(Long teacherId, TeacherDTO updatedTeacherDTO) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Profesorul nu există!"));

        List<ClassEntity> classEntities = updatedTeacherDTO.getClassNames().stream()
                .map(classEntityRepository::findByClassName)
                .filter(classEntity -> classEntity != null)
                .collect(Collectors.toList());

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
}
