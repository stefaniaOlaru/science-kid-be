package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.ExerciseDTO;
import com.example.sciencekidbe.dto.ExerciseRequest;
import com.example.sciencekidbe.model.Exercise;

public interface IApplicationService {
    ExerciseDTO addExercise(ExerciseRequest request);

    ExerciseDTO updateExercise(Long exerciseId, ExerciseRequest request);

    void deleteExercise(Long exerciseId);
}
