package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.UnitDTO;
import com.example.sciencekidbe.model.Lecture;
import com.example.sciencekidbe.model.Unit;
import com.example.sciencekidbe.repository.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnitService {
    private final UnitRepository unitRepository;

    public UnitService(UnitRepository unitRepository){
        this.unitRepository = unitRepository;
    }

    private UnitDTO convertToDTO(Unit unit){
        return new UnitDTO(
                unit.getUnitId(),
                unit.getUnitName(),
                unit.getDescription()
        );
    }

    public List<UnitDTO> getUnitByManualId(Long manualId){
        List<Unit> units = unitRepository.findByManual_ManualId(manualId);

        if(units.isEmpty()){
            throw new IllegalArgumentException("Nu s-au gasit unit-uri pt manualul specificat>");
        }

        return units.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Unit getUnitById(Long unitId){
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new IllegalArgumentException("Nu s-a gasit unit"));

        return unit;
    }
}
