package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UnitRepository extends JpaRepository<Unit, Long >{
    List<Unit> findByManual_ManualId(Long manualId);
}
