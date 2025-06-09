package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.*;
import com.example.sciencekidbe.model.*;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.UnitRepository;
import com.example.sciencekidbe.service.interfaces.IClassEntityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassEntityService implements IClassEntityService {

    private final ClassEntityRepository classEntityRepository;
    private final UnitRepository unitRepository;

    public ClassEntityService(ClassEntityRepository classEntityRepository, UnitRepository unitRepository){
        this.classEntityRepository = classEntityRepository;
        this.unitRepository = unitRepository;
    }

    public List<ClassSummaryDTO> getAllClasses(){
        return classEntityRepository
                .findAll()
                .stream()
                .map( classEntity ->  new ClassSummaryDTO(
                        classEntity.getClassId(),
                        classEntity.getClassName(),
                        classEntity.getGradeLevel()
                ))
                .collect(Collectors.toList());
    }

    public Optional<ClassEntity> getClassDetailsByClassId(Long classId){
        return classEntityRepository.findById(classId);
    }

    public ClassEntityDTO convertToClassEntityDTO(ClassEntity classEntity){
        List<StudentDTO> students = classEntity.getStudents()
                .stream()
                .map(student -> new StudentDTO(
                        student.getStudentId(),
                        student.getFullName(),
                        student.getEmail(),
                        student.getBirthdate(),
                        student.getJoinDate(),
                        student.getClassEntity().getClassName(),
                        student.getClassEntity().getClassId(),
                        student.getImagePath()
                ))
                .collect(Collectors.toList());

        List<TeacherDTO> teachers = classEntity.getTeachers()
                .stream()
                .map(teacher -> new TeacherDTO(
                        teacher.getTeacherId(),
                        teacher.getFullName(),
                        teacher.getEmail(),
                        teacher.getSubject(),
                        teacher.getPhoneNumber(),
                        teacher.getImagePath(),
                        teacher.getPdfPath(),
                        teacher.getPassword()
                ))
                .collect(Collectors.toList());

        List<ManualClassesDTO> manuals = classEntity.getManuals()
                .stream()
                .map(manual ->{
                    List<UnitDTO> units = unitRepository.findByManual_ManualId(manual.getManualId())
                            .stream()
                            .map(unit -> new UnitDTO(
                                    unit.getUnitId(),
                                    unit.getUnitName(),
                                    unit.getDescription()
                            ))
                            .collect(Collectors.toList());
                    return new ManualClassesDTO(
                             manual.getManualId(),
                             manual.getManualName(),
                             manual.getPublisher(),
                             units
                     );
                })
                .collect(Collectors.toList());

        return new ClassEntityDTO(
                classEntity.getClassName(),
                classEntity.getGradeLevel(),
                students,
                teachers,
                manuals
        );
    }
}
