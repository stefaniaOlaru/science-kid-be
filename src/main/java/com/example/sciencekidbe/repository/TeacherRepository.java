package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.ERole;
import com.example.sciencekidbe.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findByClassEntities_ClassId(Long classId);
    Optional<Teacher> findByEmail(String email);
    List<Teacher> findByRole(String role);
    Boolean existsByEmail(String email);
    List<Teacher> findByRole(ERole role);
}
