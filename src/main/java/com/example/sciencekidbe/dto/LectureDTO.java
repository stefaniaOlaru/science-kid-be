package com.example.sciencekidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LectureDTO {
    private Long lectureId;
    private String lectureName;
    private Long unitId;
    private Long observationId;
    private Long applicationId;
    private Long informationId;
}
