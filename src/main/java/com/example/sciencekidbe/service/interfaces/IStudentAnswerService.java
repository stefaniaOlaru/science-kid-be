package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.StudentAnswerDTO;
import com.example.sciencekidbe.model.StudentAnswer;

import java.time.LocalDateTime;
import java.util.List;

public interface IStudentAnswerService {
    StudentAnswer saveStudentAnswer(Long studentId, Long exerciseId, String givenAnswer, LocalDateTime answeredAt);

    List<StudentAnswerDTO> getAnswersByExerciseAndApplication(Long exerciseId, Long applicationId, Long classEntityId);
}
