package com.example.sciencekidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StudentDTO {
    private Long studentId;
    private String fullName;
    private String email;
    private LocalDate birthdate;
    private LocalDate joinDate;
    private String className;
    private Long classEntityId;
    private String imagePath;
}
