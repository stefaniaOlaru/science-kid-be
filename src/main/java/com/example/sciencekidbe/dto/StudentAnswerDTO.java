package com.example.sciencekidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudentAnswerDTO {
    private Long answerId;
    private String givenAnswer;
    private boolean correct;
    private LocalDateTime answeredAt;
    private Long exerciseId;
    private String studentName;
}
