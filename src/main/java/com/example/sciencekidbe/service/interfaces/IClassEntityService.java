package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.Manual;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.model.Teacher;

import java.util.List;

public interface IClassEntityService {

    List<ClassEntity> getAllClasses();


}
