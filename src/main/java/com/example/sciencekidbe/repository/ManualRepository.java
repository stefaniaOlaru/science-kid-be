package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.Manual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManualRepository extends JpaRepository<Manual, Long> {
    List<Manual> findByPublisher(String publisher);

    List<Manual> findByManualName(String manualName);

    List<Manual> findByClassEntities_ClassName(String className);
}
