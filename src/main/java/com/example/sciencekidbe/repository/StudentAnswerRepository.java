package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {

    List<StudentAnswer> findByExercise_ExerciseId(Long exerciseId);
    Optional<StudentAnswer> findByStudent_StudentIdAndExercise_ExerciseId(Long studentId, Long exerciseId);
    List<StudentAnswer> findByExercise_ExerciseIdAndStudent_ClassEntity_ClassId(Long exerciseId, Long classId);

}
