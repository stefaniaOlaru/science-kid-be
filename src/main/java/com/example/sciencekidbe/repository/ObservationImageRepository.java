package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.ObservationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservationImageRepository extends JpaRepository<ObservationImage, Long> {
}
