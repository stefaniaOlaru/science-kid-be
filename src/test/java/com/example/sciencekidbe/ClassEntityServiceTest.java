package com.example.sciencekidbe;

import com.example.sciencekidbe.dto.ClassEntityDTO;
import com.example.sciencekidbe.dto.ClassSummaryDTO;
import com.example.sciencekidbe.model.*;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.UnitRepository;
import com.example.sciencekidbe.service.ClassEntityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassEntityServiceTest {

    @Mock
    private ClassEntityRepository classEntityRepository;

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private ClassEntityService classEntityService;

    @Test
    void getAllClasses_shouldReturnClassSummaries() {
        ClassEntity class1 = new ClassEntity();
        class1.setClassId(1L);
        class1.setClassName("Class A");
        class1.setGradeLevel(5);

        ClassEntity class2 = new ClassEntity();
        class2.setClassId(2L);
        class2.setClassName("Class B");
        class2.setGradeLevel(6);

        when(classEntityRepository.findAll()).thenReturn(List.of(class1, class2));

        List<ClassSummaryDTO> result = classEntityService.getAllClasses();

        assertEquals(2, result.size());
        assertEquals("Class A", result.get(0).getClassName());
        assertEquals(5, result.get(0).getGradeLevel());
    }

    @Test
    void getClassDetailsByClassId_shouldReturnClassEntity() {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassId(1L);

        when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));

        Optional<ClassEntity> result = classEntityService.getClassDetailsByClassId(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getClassId());
    }

    @Test
    void convertToClassEntityDTO_shouldMapAllFields() {
        // Setup ClassEntity with students, teachers, manuals
        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassId(1L);
        classEntity.setClassName("Class A");
        classEntity.setGradeLevel(5);

        Student student = new Student();
        student.setStudentId(1L);
        student.setFullName("John Doe");
        student.setEmail("john@example.com");
        student.setBirthdate(LocalDate.of(2010, 1, 1));
        student.setJoinDate(LocalDate.of(2022, 9, 1));
        student.setClassEntity(classEntity);

        Teacher teacher = new Teacher();
        teacher.setTeacherId(1L);
        teacher.setFullName("Jane Smith");
        teacher.setEmail("jane@example.com");
        teacher.setSubject("Math");
        teacher.setPhoneNumber("0123456789");

        Manual manual = new Manual();
        manual.setManualId(1L);
        manual.setManualName("Math Manual");
        manual.setPublisher("Publisher");

        Unit unit = new Unit();
        unit.setUnitId(1L);
        unit.setUnitName("Algebra");
        unit.setDescription("Basic algebra");
        unit.setManual(manual);

        classEntity.setStudents(List.of(student));
        classEntity.setTeachers(List.of(teacher));
        classEntity.setManuals(List.of(manual));

        when(unitRepository.findByManual_ManualId(manual.getManualId())).thenReturn(List.of(unit));

        ClassEntityDTO dto = classEntityService.convertToClassEntityDTO(classEntity);

        assertEquals("Class A", dto.getClassName());
        assertEquals(5, dto.getGradeLevel());
        assertEquals(1, dto.getStudents().size());
        assertEquals("John Doe", dto.getStudents().get(0).getFullName());
        assertEquals(1, dto.getTeachers().size());
        assertEquals("Jane Smith", dto.getTeachers().get(0).getFullName());
        assertEquals(1, dto.getManuals().size());
        assertEquals("Math Manual", dto.getManuals().get(0).getManualName());
        assertEquals(1, dto.getManuals().get(0).getUnits().size());
        assertEquals("Algebra", dto.getManuals().get(0).getUnits().get(0).getUnitName());
    }
}
