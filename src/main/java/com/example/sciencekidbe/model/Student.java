package com.example.sciencekidbe.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Student")
@Entity
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @NonNull
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NonNull
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NonNull
    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @NonNull
    @Column(name = "joinDate")
    private LocalDate joinDate;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    @JsonManagedReference
    private ClassEntity classEntity;
}
