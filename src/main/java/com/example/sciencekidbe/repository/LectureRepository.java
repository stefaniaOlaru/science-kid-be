package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findByUnit_UnitId(Long unitId);

}
