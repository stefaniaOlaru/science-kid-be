package com.example.sciencekidbe;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.sciencekidbe.dto.StudentAnswerDTO;
import com.example.sciencekidbe.model.*;
import com.example.sciencekidbe.repository.*;
import com.example.sciencekidbe.service.StudentAnswerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

class StudentAnswerServiceTest {

    @Mock
    private StudentAnswerRepository studentAnswerRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentAnswerService studentAnswerService;

    private Student student;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        student = new Student();
        student.setStudentId(1L);
        student.setFullName("John Doe");

        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassId(100L);
        student.setClassEntity(classEntity);

        exercise = new Exercise();
        exercise.setExerciseId(10L);
        exercise.setCorrectAnswer("42");

        Application app = new Application();
        app.setApplicationId(555L);
        exercise.setApplication(app);
    }

    // ================= saveStudentAnswer =================
    @Test
    void saveStudentAnswer_shouldSaveSuccessfully() {
        LocalDateTime now = LocalDateTime.now();

        when(exerciseRepository.findById(10L)).thenReturn(Optional.of(exercise));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentAnswerRepository.findByStudent_StudentIdAndExercise_ExerciseId(1L, 10L))
                .thenReturn(Optional.empty());

        StudentAnswer saved = new StudentAnswer();
        saved.setAnswerId(99L);
        saved.setStudent(student);
        saved.setExercise(exercise);
        saved.setGivenAnswer("42");
        saved.setCorrect(true);
        saved.setAnsweredAt(now);

        when(studentAnswerRepository.save(any(StudentAnswer.class))).thenReturn(saved);

        StudentAnswer result = studentAnswerService.saveStudentAnswer(1L, 10L, "42", now);

        assertEquals(saved.getAnswerId(), result.getAnswerId());
        assertTrue(result.isCorrect());
        verify(studentAnswerRepository).save(any(StudentAnswer.class));
    }

    @Test
    void saveStudentAnswer_shouldThrowIfAlreadyAnswered() {
        when(exerciseRepository.findById(10L)).thenReturn(Optional.of(exercise));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentAnswerRepository.findByStudent_StudentIdAndExercise_ExerciseId(1L, 10L))
                .thenReturn(Optional.of(new StudentAnswer()));

        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> studentAnswerService.saveStudentAnswer(1L, 10L, "42", now));
        assertTrue(ex.getMessage().contains("S-a raspuns deja"));
    }

    @Test
    void saveStudentAnswer_shouldThrowIfStudentOrExerciseNotFound() {
        when(exerciseRepository.findById(10L)).thenReturn(Optional.empty());
        LocalDateTime now = LocalDateTime.now();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> studentAnswerService.saveStudentAnswer(1L, 10L, "42", now));
        assertTrue(ex.getMessage().contains("Exercițiul nu există"));
    }

    @Test
    void saveStudentAnswer_shouldThrowIfAnsweredAtOutOfTimeWindow() {
        when(exerciseRepository.findById(10L)).thenReturn(Optional.of(exercise));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentAnswerRepository.findByStudent_StudentIdAndExercise_ExerciseId(1L, 10L))
                .thenReturn(Optional.empty());

        LocalDateTime tooEarly = LocalDateTime.now().minusMinutes(61);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> studentAnswerService.saveStudentAnswer(1L, 10L, "42", tooEarly));
        assertTrue(ex.getMessage().contains("Răspunsul trebuie să fie dat"));

        LocalDateTime future = LocalDateTime.now().plusMinutes(1);
        ex = assertThrows(IllegalArgumentException.class,
                () -> studentAnswerService.saveStudentAnswer(1L, 10L, "42", future));
        assertTrue(ex.getMessage().contains("Răspunsul trebuie să fie dat"));
    }

    // ================= getAnswersByExerciseAndApplication =================
    @Test
    void getAnswersByExerciseAndApplication_shouldReturnFilteredDTOs() {
        when(exerciseRepository.findById(10L)).thenReturn(Optional.of(exercise));

        StudentAnswer answer = new StudentAnswer();
        answer.setAnswerId(5L);
        answer.setStudent(student);
        answer.setExercise(exercise);
        answer.setGivenAnswer("42");
        answer.setCorrect(true);
        answer.setAnsweredAt(LocalDateTime.now());

        when(studentAnswerRepository.findByExercise_ExerciseId(10L))
                .thenReturn(List.of(answer));

        List<StudentAnswerDTO> results = studentAnswerService.getAnswersByExerciseAndApplication(
                10L, 555L, 100L
        );

        assertEquals(1, results.size());
        assertEquals(5L, results.get(0).getAnswerId());
    }

    @Test
    void getAnswersByExerciseAndApplication_shouldThrowIfWrongApplication() {
        when(exerciseRepository.findById(10L)).thenReturn(Optional.of(exercise));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> studentAnswerService.getAnswersByExerciseAndApplication(10L, 999L, 100L));
        assertTrue(ex.getMessage().contains("nu aparține aplicației"));
    }

    // ================= getAnswerStats =================
    @Test
    void getAnswerStats_shouldReturnCorrectStats() {
        Student student2 = new Student();
        student2.setStudentId(2L);
        student2.setClassEntity(student.getClassEntity());

        List<Student> allStudents = List.of(student, student2);

        StudentAnswer correctAnswer = new StudentAnswer();
        correctAnswer.setStudent(student);
        correctAnswer.setExercise(exercise);
        correctAnswer.setCorrect(true);

        StudentAnswer wrongAnswer = new StudentAnswer();
        wrongAnswer.setStudent(student2);
        wrongAnswer.setExercise(exercise);
        wrongAnswer.setCorrect(false);

        when(exerciseRepository.findById(10L)).thenReturn(Optional.of(exercise));
        when(studentRepository.findByClassEntity_ClassId(100L)).thenReturn(allStudents);
        when(studentAnswerRepository.findByExercise_ExerciseIdAndStudent_ClassEntity_ClassId(10L, 100L))
                .thenReturn(List.of(correctAnswer, wrongAnswer));

        Map<String, Long> stats = studentAnswerService.getAnswerStats(10L, 555L, 100L);

        assertEquals(1L, stats.get("correct"));
        assertEquals(1L, stats.get("incorrect"));
        assertEquals(0L, stats.get("absent"));
    }

    // ================= getIncorrectCountsByExercise =================
    @Test
    void getIncorrectCountsByExercise_shouldReturnCounts() {
        Exercise ex1 = new Exercise();
        ex1.setExerciseId(1L);
        ex1.setQuestion("Q1");
        ex1.setApplication(exercise.getApplication());

        Exercise ex2 = new Exercise();
        ex2.setExerciseId(2L);
        ex2.setQuestion("Q2");
        ex2.setApplication(exercise.getApplication());

        when(exerciseRepository.findByApplication_ApplicationId(555L)).thenReturn(List.of(ex1, ex2));

        StudentAnswer wrongAnswer = new StudentAnswer();
        wrongAnswer.setExercise(ex1);
        wrongAnswer.setCorrect(false);

        when(studentAnswerRepository.findByExercise_ExerciseIdAndStudent_ClassEntity_ClassId(1L, 100L))
                .thenReturn(List.of(wrongAnswer));
        when(studentAnswerRepository.findByExercise_ExerciseIdAndStudent_ClassEntity_ClassId(2L, 100L))
                .thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = studentAnswerService.getIncorrectCountsByExercise(555L, 100L);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).get("incorrect"));
        assertEquals(0L, result.get(1).get("incorrect"));
    }
}

