package com.example.sciencekidbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Entity
@Table(name = "Observation")
public class Observation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long observationId;

    @OneToOne(mappedBy = "observation")
    private Lecture lecture;

    @OneToMany(mappedBy = "observation", cascade = CascadeType.ALL)
    private List<ObservationImage> images;

    @Column(name = "audio_path")
    private String audioPath;
}
