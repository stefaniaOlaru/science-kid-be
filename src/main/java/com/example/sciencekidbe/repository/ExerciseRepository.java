package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByApplication_ApplicationId(Long applicatioId);

}
