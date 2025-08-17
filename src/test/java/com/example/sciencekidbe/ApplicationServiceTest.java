package com.example.sciencekidbe;

import com.example.sciencekidbe.dto.ExerciseDTO;
import com.example.sciencekidbe.dto.ExerciseRequest;
import com.example.sciencekidbe.model.Application;
import com.example.sciencekidbe.model.Exercise;
import com.example.sciencekidbe.repository.ApplicationRepository;
import com.example.sciencekidbe.repository.ExerciseRepository;
import com.example.sciencekidbe.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ==================== addExercise ====================
    @Test
    void addExercise_success() {
        Application app = new Application();
        app.setExercises(new ArrayList<>());

        ExerciseRequest request = new ExerciseRequest();
        request.setApplicationId(1L);
        request.setQuestion("Q1");
        request.setCorrectAnswer("A1");

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(i -> i.getArgument(0));

        Exercise result = applicationService.addExercise(request);

        assertNotNull(result);
        assertEquals("Q1", result.getQuestion());
        assertEquals("A1", result.getCorrectAnswer());
        assertTrue(app.getExercises().contains(result));
    }

    @Test
    void addExercise_applicationNotFound() {
        ExerciseRequest request = new ExerciseRequest();
        request.setApplicationId(1L);

        when(applicationRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> applicationService.addExercise(request));
        assertEquals("Aplicația nu există.", ex.getMessage());
    }

    @Test
    void addExercise_verifySaveCalled() {
        Application app = new Application();
        app.setExercises(new ArrayList<>());

        ExerciseRequest request = new ExerciseRequest();
        request.setApplicationId(1L);
        request.setQuestion("Q1");
        request.setCorrectAnswer("A1");

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(i -> i.getArgument(0));

        applicationService.addExercise(request);

        verify(exerciseRepository, times(1)).save(any(Exercise.class));
    }

    // ==================== updateExercise ====================
    @Test
    void updateExercise_success() {
        Exercise exercise = new Exercise();
        exercise.setExerciseId(1L);
        exercise.setQuestion("OldQ");
        exercise.setCorrectAnswer("OldA");

        ExerciseRequest request = new ExerciseRequest();
        request.setQuestion("NewQ");
        request.setCorrectAnswer("NewA");

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(i -> i.getArgument(0));

        ExerciseDTO dto = applicationService.updateExercise(1L, request);

        assertEquals(1L, dto.getExerciseId());
        assertEquals("NewQ", dto.getQuestion());
        assertEquals("NewA", dto.getCorrectAnswer());
    }

    @Test
    void updateExercise_exerciseNotFound() {
        ExerciseRequest request = new ExerciseRequest();
        when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> applicationService.updateExercise(1L, request));
        assertEquals("Exercițiul nu există.", ex.getMessage());
    }

    @Test
    void updateExercise_verifySaveCalled() {
        Exercise exercise = new Exercise();
        exercise.setExerciseId(1L);

        ExerciseRequest request = new ExerciseRequest();
        request.setQuestion("Q");
        request.setCorrectAnswer("A");

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(i -> i.getArgument(0));

        applicationService.updateExercise(1L, request);

        verify(exerciseRepository, times(1)).save(exercise);
    }

    // ==================== deleteExercise ====================
    @Test
    void deleteExercise_success() {
        Exercise exercise = new Exercise();
        exercise.setExerciseId(1L);

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        applicationService.deleteExercise(1L);

        verify(exerciseRepository, times(1)).delete(exercise);
    }

    @Test
    void deleteExercise_exerciseNotFound() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> applicationService.deleteExercise(1L));
        assertEquals("Exercițiul nu există.", ex.getMessage());
    }

    @Test
    void deleteExercise_verifyDeleteCalled() {
        Exercise exercise = new Exercise();
        exercise.setExerciseId(1L);

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        applicationService.deleteExercise(1L);

        verify(exerciseRepository, times(1)).delete(exercise);
    }
}
