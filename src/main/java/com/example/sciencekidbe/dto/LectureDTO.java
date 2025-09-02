package com.example.sciencekidbe.dto;

import com.example.sciencekidbe.model.QrCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private List<String> qrCodes;
}
