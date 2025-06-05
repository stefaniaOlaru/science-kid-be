package com.example.sciencekidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ObservationImageDTO {
    private Long imageId;
    private String imagePath;
}
