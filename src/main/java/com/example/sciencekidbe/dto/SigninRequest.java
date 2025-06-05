package com.example.sciencekidbe.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SigninRequest {
    private String email;
    private String password;
}
