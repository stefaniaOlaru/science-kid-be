package com.example.sciencekidbe.dto;

import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.Teacher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherDTO {
    private Long teacherId;
    private String fullName;
    private String email;
    private String subject;
    private String phoneNumber;
    private List<String> classNames;
    private List<Long> classEntityIds;

    public TeacherDTO(Long teacherId, String fullName, String email, String subject, String phoneNumber){
        this.teacherId = teacherId;
        this.fullName = fullName;
        this.email = email;
        this.subject = subject;
        this.phoneNumber = phoneNumber;
    }
}
