package com.example.sciencekidbe.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@Setter
@Getter
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
