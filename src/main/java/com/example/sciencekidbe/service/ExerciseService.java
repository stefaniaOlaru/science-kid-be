package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ExerciseDTO;
import com.example.sciencekidbe.model.Exercise;
import com.example.sciencekidbe.repository.ExerciseRepository;
import com.example.sciencekidbe.service.interfaces.IExerciseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseService implements IExerciseService {
    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository){
        this.exerciseRepository = exerciseRepository;
    }

    private ExerciseDTO convertToDTO(Exercise exercise){
        return new ExerciseDTO(
                exercise.getExerciseId(),
                exercise.getQuestion(),
                exercise.getCorrectAnswer()
        );
    }

    public List<ExerciseDTO> getExercisesByApplication(Long applicationId) {
        List<Exercise> exercises = exerciseRepository.findByApplication_ApplicationId(applicationId);

        if(exercises.isEmpty()){
            throw new IllegalArgumentException("Nu au fost gasite exercitii pentru aplicatia respectiva");
        }

        return exercises.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
