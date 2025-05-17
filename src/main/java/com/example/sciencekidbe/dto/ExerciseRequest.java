package com.example.sciencekidbe.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseRequest {
    private Long applicationId;
    private String question;
    private String correctAnswer;
}
