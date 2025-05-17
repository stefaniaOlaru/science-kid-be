package com.example.sciencekidbe.repository;

import com.example.sciencekidbe.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByInformation_InformationId(Long informationId);
}
