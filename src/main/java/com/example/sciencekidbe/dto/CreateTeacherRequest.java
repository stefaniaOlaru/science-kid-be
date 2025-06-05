package com.example.sciencekidbe.dto;

import com.example.sciencekidbe.model.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTeacherRequest {
    private String fullName;
    private String email;
    private String subject;
    private String phoneNumber;
    private String password;
    private ERole role;
    private List<Long> classEntityIds;
}
