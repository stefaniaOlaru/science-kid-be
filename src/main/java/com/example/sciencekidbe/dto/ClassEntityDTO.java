package com.example.sciencekidbe.dto;

import com.example.sciencekidbe.model.Teacher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClassEntityDTO {
    private String className;
    private Integer gradeLevel;
    private List<StudentDTO> students;
    private List<TeacherDTO> teachers;
    private List<ManualClassesDTO> manuals;
}
