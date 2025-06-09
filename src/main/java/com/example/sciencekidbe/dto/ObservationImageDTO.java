package com.example.sciencekidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ObservationImageDTO {
    private Long imageId;
    private String imagePath;
    private List<ObservationQuestionDTO> questions;
}
