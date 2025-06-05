package com.example.sciencekidbe.dto;

import com.example.sciencekidbe.model.ERole;
import com.example.sciencekidbe.model.Teacher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private ERole role;

    public UserDTO(Teacher teacher) {
        this.id = teacher.getTeacherId();
        this.name = teacher.getFullName();
        this.role = teacher.getRole();
    }
}
