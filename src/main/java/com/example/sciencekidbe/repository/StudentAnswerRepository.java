package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByStudent_StudentIdAndExercise_Application_ApplicationId(Long studentId, Long applicationId);
    List<StudentAnswer> findByExercise_ExerciseId(Long exerciseId);
}
