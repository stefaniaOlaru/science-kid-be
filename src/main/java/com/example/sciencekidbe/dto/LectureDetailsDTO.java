package com.example.sciencekidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LectureDetailsDTO {
    private Long lectureId;
    private Long unitId;
    private Long observationId;
    private Long applicationId;
    private Long informationId;
}
