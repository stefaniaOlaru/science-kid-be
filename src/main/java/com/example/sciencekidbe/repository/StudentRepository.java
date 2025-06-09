package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long>{

    Optional<Student> findByEmail(String email);
    List<Student> findByClassEntity_ClassId(Long classId);

}
