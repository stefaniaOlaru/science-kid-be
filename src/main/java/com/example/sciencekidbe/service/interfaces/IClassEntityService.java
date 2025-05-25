package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.ClassSummaryDTO;
import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.Manual;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.model.Teacher;

import java.util.List;
import java.util.Optional;

public interface IClassEntityService {

    List<ClassSummaryDTO> getAllClasses();

    Optional<ClassEntity> getClassDetailsByClassId(Long classId);

}
