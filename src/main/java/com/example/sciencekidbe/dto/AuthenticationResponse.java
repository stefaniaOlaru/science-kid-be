package com.example.sciencekidbe.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String role;
    private Long teacherId;

    public AuthenticationResponse(String token, String role, Long teacherId) {
        this.token = token;
        this.role = role;
        this.teacherId = teacherId;
    }
}
