package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.UnitDTO;

import java.util.List;

public interface IUnitService {
    List<UnitDTO> getUnitByManualId(Long manualId);
}
