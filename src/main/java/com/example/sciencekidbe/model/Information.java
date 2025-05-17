package com.example.sciencekidbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Information")
public class Information {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long informationId;

    @OneToOne(mappedBy = "information")
    private Lecture lecture;

    @OneToMany(mappedBy = "information", cascade = CascadeType.ALL)
    private List<Content> contents;
}
