package com.example.sciencekidbe.dto;

import com.example.sciencekidbe.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateStudentRequest {
    private String fullName;
    private String email;
    private LocalDate birthdate;
    private Long classEntityId;
}
