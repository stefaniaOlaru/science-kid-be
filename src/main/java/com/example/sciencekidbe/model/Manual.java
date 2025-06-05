package com.example.sciencekidbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "Manual")
public class Manual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long manualId;

    @Column(name = "manual_name")
    private String manualName;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "pdf_path")
    private String pdfPath;

    @ManyToMany
    @JoinTable(
            name = "manual_class",
            joinColumns = @JoinColumn(name = "manual_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    @JsonIgnore
    private List<ClassEntity> classEntities;

    @OneToMany(mappedBy = "manual", cascade = CascadeType.ALL)
    private List<Unit> units;
}
