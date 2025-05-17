package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.ObservationQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservationQuestionRepository extends JpaRepository<ObservationQuestion, Long> {
}
