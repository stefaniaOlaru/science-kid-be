package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ExerciseDTO;
import com.example.sciencekidbe.dto.ExerciseRequest;
import com.example.sciencekidbe.dto.StudentAnswerDTO;
import com.example.sciencekidbe.model.Application;
import com.example.sciencekidbe.model.Exercise;
import com.example.sciencekidbe.model.StudentAnswer;
import com.example.sciencekidbe.repository.ApplicationRepository;
import com.example.sciencekidbe.repository.ExerciseRepository;
import com.example.sciencekidbe.repository.StudentAnswerRepository;
import com.example.sciencekidbe.service.interfaces.IApplicationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ApplicationService implements IApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ExerciseRepository exerciseRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              ExerciseRepository exerciseRepository,
                              StudentAnswerRepository studentAnswerRepository ){
        this.applicationRepository = applicationRepository;
        this.exerciseRepository = exerciseRepository;
        this.studentAnswerRepository = studentAnswerRepository;
    }

    public ExerciseDTO addExercise(ExerciseRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("Aplicația nu există."));

        Exercise exercise = new Exercise();
        exercise.setQuestion(request.getQuestion());
        exercise.setCorrectAnswer(request.getCorrectAnswer());
        exercise.setApplication(application);

        application.getExercises().add(exercise);

        Exercise saved = exerciseRepository.save(exercise);
        return convertToDTO(saved);
    }

    public ExerciseDTO convertToDTO(Exercise exercise) {
        return new ExerciseDTO(
                exercise.getExerciseId(),
                exercise.getQuestion(),
                exercise.getCorrectAnswer()
        );
    }

    @Transactional
    public ExerciseDTO updateExercise(Long exerciseId, ExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercițiul nu există."));

        boolean changed =
                !Objects.equals(exercise.getQuestion(), request.getQuestion()) ||
                        !Objects.equals(exercise.getCorrectAnswer(), request.getCorrectAnswer());

        Long applicationId = exercise.getApplication().getApplicationId();

        exercise.setQuestion(request.getQuestion());
        exercise.setCorrectAnswer(request.getCorrectAnswer());

        Exercise saved = exerciseRepository.save(exercise);

        if (changed) {
            studentAnswerRepository.deleteAllByApplicationId(applicationId);
        }

        return new ExerciseDTO(saved.getExerciseId(),saved.getQuestion(),saved.getCorrectAnswer());
    }

    public void deleteExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercițiul nu există."));

        exerciseRepository.delete(exercise);
    }
}
