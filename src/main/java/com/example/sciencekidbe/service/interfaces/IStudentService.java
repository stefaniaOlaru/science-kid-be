package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.model.Student;

import java.util.List;

public interface IStudentService {

       Student addStudent(StudentDTO studentDTO);

       List<StudentDTO> getAllStudent();

       Student updateStudent(StudentDTO student);

       void deleteStudent(Long studentId);

       String findClassNameByEmail(String email);
}
