package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ExerciseDTO;
import com.example.sciencekidbe.dto.ExerciseRequest;
import com.example.sciencekidbe.model.Application;
import com.example.sciencekidbe.model.Exercise;
import com.example.sciencekidbe.repository.ApplicationRepository;
import com.example.sciencekidbe.repository.ExerciseRepository;
import com.example.sciencekidbe.service.interfaces.IApplicationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService implements IApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ExerciseRepository exerciseRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              ExerciseRepository exerciseRepository){
        this.applicationRepository = applicationRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public Exercise addExercise(ExerciseRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("Aplicația nu există."));

        Exercise exercise = new Exercise();
        exercise.setQuestion(request.getQuestion());
        exercise.setCorrectAnswer(request.getCorrectAnswer());
        exercise.setApplication(application);

        application.getExercises().add(exercise);

        return exerciseRepository.save(exercise);
    }

    public ExerciseDTO updateExercise(Long exerciseId, ExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercițiul nu există."));

        exercise.setQuestion(request.getQuestion());
        exercise.setCorrectAnswer(request.getCorrectAnswer());

        Exercise saved = exerciseRepository.save(exercise);

        return new ExerciseDTO(saved.getExerciseId(),saved.getQuestion(),saved.getCorrectAnswer());
    }

    public void deleteExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercițiul nu există."));

        exerciseRepository.delete(exercise);
    }
}
