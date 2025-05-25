package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.LectureDTO;

import java.util.List;

public interface ILectureService {
    List<LectureDTO> getLecturesByUnitId(Long unitId);
}
