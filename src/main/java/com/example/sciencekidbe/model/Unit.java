package com.example.sciencekidbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Unit")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long unitId;

    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "manual_id", nullable = false)
    private Manual manual;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL)
    private List<Lecture> lectures;
}
