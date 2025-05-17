package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findByClassEntities_ClassId(Long classId);
}
