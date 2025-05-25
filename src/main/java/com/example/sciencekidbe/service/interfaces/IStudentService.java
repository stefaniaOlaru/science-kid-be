package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.ManualDTO;
import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.model.Student;

import java.util.List;

public interface IStudentService {

       Student addStudent(StudentDTO studentDTO);

       StudentDTO getStudentById(Long studentId);

       List<StudentDTO> getAllStudents();

       Student updateStudent(StudentDTO student);

       void deleteStudent(Long studentId);

       List<ManualDTO> getManualForStudent(Long classId);
}
