package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.StudentAnswerDTO;
import com.example.sciencekidbe.dto.StudentAnswerRequest;
import com.example.sciencekidbe.model.Exercise;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.model.StudentAnswer;
import com.example.sciencekidbe.repository.ExerciseRepository;
import com.example.sciencekidbe.repository.StudentAnswerRepository;
import com.example.sciencekidbe.repository.StudentRepository;
import com.example.sciencekidbe.service.interfaces.IStudentAnswerService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentAnswerService implements IStudentAnswerService {
    private final StudentAnswerRepository studentAnswerRepository;
    private final ExerciseRepository exerciseRepository;
    private final StudentRepository studentRepository;

    public StudentAnswerService(StudentAnswerRepository studentAnswerRepository,
                                ExerciseRepository exerciseRepository,
                                StudentRepository studentRepository) {
        this.studentAnswerRepository = studentAnswerRepository;
        this.exerciseRepository = exerciseRepository;
        this.studentRepository = studentRepository;
    }

    public StudentAnswer saveStudentAnswer(Long studentId, Long exerciseId, String givenAnswer, LocalDateTime answeredAt) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercițiul nu există!"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Studentul nu există!"));

        LocalDateTime date = LocalDateTime.now();
        if (answeredAt.isBefore(date.minusMinutes(60)) || answeredAt.isAfter(date)) {
            throw new IllegalArgumentException("Răspunsul trebuie să fie dat în timpul orei de curs (60 minute)!");
        }

        Optional<StudentAnswer> studentAnswerExists = studentAnswerRepository.findByStudent_StudentIdAndExercise_ExerciseId(studentId, exerciseId);

        if (studentAnswerExists.isPresent()) {
            throw new IllegalArgumentException("S-a raspuns deja la acest exercitiu");
        }

        StudentAnswer studentAnswer = new StudentAnswer();
        studentAnswer.setStudent(student);
        studentAnswer.setExercise(exercise);
        studentAnswer.setGivenAnswer(givenAnswer);
        studentAnswer.setCorrect(givenAnswer.equalsIgnoreCase(exercise.getCorrectAnswer()));
        studentAnswer.setAnsweredAt(answeredAt);

        return studentAnswerRepository.save(studentAnswer);
    }

    @Transactional
    public List<StudentAnswer> saveStudentAnswers(List<StudentAnswerRequest> requests) {
        List<StudentAnswer> savedAnswers = new ArrayList<>();
        for (StudentAnswerRequest request : requests) {
            Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                    .orElseThrow(() -> new IllegalArgumentException("Exercițiul nu există!"));

            Student student = studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("Studentul nu există!"));

            Optional<StudentAnswer> studentAnswerExists =
                    studentAnswerRepository.
                            findByStudent_StudentIdAndExercise_ExerciseId(request.getStudentId(), request.getExerciseId());

            if (studentAnswerExists.isPresent()) {
                throw new IllegalArgumentException("S-a raspuns deja la acest exercitiu");
            }

            boolean isCorrect = exercise.getCorrectAnswer().equalsIgnoreCase(request.getGivenAnswer());

            StudentAnswer studentAnswer = new StudentAnswer();
            studentAnswer.setStudent(student);
            studentAnswer.setExercise(exercise);
            studentAnswer.setGivenAnswer(request.getGivenAnswer());
            studentAnswer.setCorrect(isCorrect);
            studentAnswer.setAnsweredAt(LocalDateTime.now());

            savedAnswers.add(studentAnswerRepository.save(studentAnswer));
        }
        return savedAnswers;
    }

    private StudentAnswerDTO convertToDTO(StudentAnswer studentAnswer) {
        return new StudentAnswerDTO(
                studentAnswer.getAnswerId(),
                studentAnswer.getGivenAnswer(),
                studentAnswer.isCorrect(),
                studentAnswer.getAnsweredAt(),
                studentAnswer.getExercise().getExerciseId(),
                studentAnswer.getStudent().getFullName()
        );
    }

    public List<StudentAnswerDTO> getAnswersByExerciseAndApplication(Long exerciseId, Long applicationId, Long classEntityId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercițiul nu există."));


        if (!exercise.getApplication().getApplicationId().equals(applicationId)) {
            throw new IllegalArgumentException("Exercițiul nu aparține aplicației specificate.");
        }

        List<StudentAnswer> answers = studentAnswerRepository.findByExercise_ExerciseId(exerciseId);

        List<StudentAnswer> filtered = answers.stream()
                .filter(ans -> ans.getStudent().getClassEntity().getClassId().equals(classEntityId))
                .toList();
        return filtered.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Map<String, Long> getAnswerStats(Long exerciseId, Long applicationId, Long classId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercițiul nu există!"));

        if (!exercise.getApplication().getApplicationId().equals(applicationId)) {
            throw new IllegalArgumentException("Exercițiul nu aparține aplicației specificate!");
        }

        List<Student> allStudents = studentRepository.findByClassEntity_ClassId(classId);
        List<StudentAnswer> answers = studentAnswerRepository
                .findByExercise_ExerciseIdAndStudent_ClassEntity_ClassId(exerciseId, classId);

        Set<Long> answeredStudentIds = answers.stream()
                .map(ans -> ans.getStudent().getStudentId())
                .collect(Collectors.toSet());

        long correct = answers.stream().filter(StudentAnswer::isCorrect).count();
        long incorrect = answers.size() - correct;
        long absent = allStudents.stream()
                .filter(student -> !answeredStudentIds.contains(student.getStudentId()))
                .count();

        return Map.of(
                "correct", correct,
                "incorrect", incorrect,
                "absent", absent
        );
    }

    public List<Map<String, Object>> getIncorrectCountsByExercise(Long applicationId, Long classId) {
        List<Exercise> exercises = exerciseRepository.findByApplication_ApplicationId(applicationId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Exercise ex : exercises) {
            List<StudentAnswer> answers = studentAnswerRepository
                    .findByExercise_ExerciseIdAndStudent_ClassEntity_ClassId(ex.getExerciseId(), classId);

            long incorrect = answers.stream().filter(a -> !a.isCorrect()).count();

            result.add(Map.of(
                    "exerciseId", ex.getExerciseId(),
                    "question", ex.getQuestion(),
                    "incorrect", incorrect
            ));
        }

        return result;
    }

}
