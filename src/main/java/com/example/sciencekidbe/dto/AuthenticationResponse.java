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

    // If you want, keep this all args constructor, but builder uses it internally
    public AuthenticationResponse(String token, String role, Long teacherId) {
        this.token = token;
        this.role = role;
        this.teacherId = teacherId;
    }
}
