package com.example.sciencekidbe.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class StudentAuthenticationResponse extends AuthenticationResponse {
    private String password;
}
