package com.example.sciencekidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ObservationQuestionDTO {
    private long questionId;
    private String questionContent;
    private String answer;
}
