package com.example.sciencekidbe.repository;


import com.example.sciencekidbe.model.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long>{
    Optional<Observation> findByLecture_LectureId(Long lectureId);

}
