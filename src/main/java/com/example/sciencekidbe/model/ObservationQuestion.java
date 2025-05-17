package com.example.sciencekidbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "ObservationQuestion")
public class ObservationQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(name = "question_content")
    private String questionContent;

    @Column(name = "answer")
    private String answer;

    @ManyToOne
    @JoinColumn(name = "image_id", nullable = false)
    private ObservationImage image;
}
