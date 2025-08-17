package com.example.sciencekidbe;

import com.example.sciencekidbe.dto.ExerciseDTO;
import com.example.sciencekidbe.model.Exercise;
import com.example.sciencekidbe.repository.ExerciseRepository;
import com.example.sciencekidbe.service.ExerciseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    void getExercisesByApplication_shouldReturnExerciseDTOs_whenExercisesExist() {
        Exercise exercise1 = new Exercise();
        exercise1.setExerciseId(1L);
        exercise1.setQuestion("Question 1");
        exercise1.setCorrectAnswer("Answer 1");

        Exercise exercise2 = new Exercise();
        exercise2.setExerciseId(2L);
        exercise2.setQuestion("Question 2");
        exercise2.setCorrectAnswer("Answer 2");

        when(exerciseRepository.findByApplication_ApplicationId(1L))
                .thenReturn(List.of(exercise1, exercise2));

        List<ExerciseDTO> result = exerciseService.getExercisesByApplication(1L);

        assertEquals(2, result.size());
        assertEquals("Question 1", result.get(0).getQuestion());
        assertEquals("Answer 2", result.get(1).getCorrectAnswer());
    }

    @Test
    void getExercisesByApplication_shouldThrowException_whenNoExercisesFound() {
        when(exerciseRepository.findByApplication_ApplicationId(1L))
                .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                exerciseService.getExercisesByApplication(1L)
        );

        assertEquals("Nu au fost gasite exercitii pentru aplicatia respectiva", exception.getMessage());
    }

    @Test
    void convertToDTO_shouldMapExerciseFieldsCorrectly() throws Exception {
        Exercise exercise = new Exercise();
        exercise.setExerciseId(1L);
        exercise.setQuestion("Sample Question");
        exercise.setCorrectAnswer("Sample Answer");

        Method convertToDTO = ExerciseService.class.getDeclaredMethod("convertToDTO", Exercise.class);
        convertToDTO.setAccessible(true);

        ExerciseDTO dto = (ExerciseDTO) convertToDTO.invoke(exerciseService, exercise);

        assertEquals(1L, dto.getExerciseId());
        assertEquals("Sample Question", dto.getQuestion());
        assertEquals("Sample Answer", dto.getCorrectAnswer());
    }
}

