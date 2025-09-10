package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {

}
