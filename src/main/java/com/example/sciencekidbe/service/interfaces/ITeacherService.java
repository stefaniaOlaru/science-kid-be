package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.ClassSummaryDTO;
import com.example.sciencekidbe.dto.TeacherDTO;
import com.example.sciencekidbe.model.Teacher;

import java.util.List;

public interface ITeacherService {
    List<TeacherDTO> getAllTeacher();
    void deleteTeacher(Long teacherId);

    Teacher updateTeacher(Long teacherId, TeacherDTO updatedTeacherDTO);

    List<TeacherDTO> getTeachersByClass(Long classId);

    List<ClassSummaryDTO> getClassesForTeacher(Long teacherId);
}
