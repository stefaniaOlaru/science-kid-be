package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.ExerciseDTO;

import java.util.List;

public interface IExerciseService {
    List<ExerciseDTO> getExercisesByApplication(Long applicationId);
}
