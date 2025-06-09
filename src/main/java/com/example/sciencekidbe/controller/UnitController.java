package com.example.sciencekidbe.controller;


import com.example.sciencekidbe.dto.UnitDTO;
import com.example.sciencekidbe.model.Lecture;
import com.example.sciencekidbe.model.Unit;
import com.example.sciencekidbe.service.UnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@CrossOrigin(origins = "*")
public class UnitController {
    private final UnitService unitService;

    public UnitController(UnitService unitService){
        this.unitService = unitService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESOR')")
    @GetMapping("/manual/{manualId}")
    public ResponseEntity<List<UnitDTO>> getUnitByManualId(@PathVariable Long manualId){
        try{
            List<UnitDTO> units = unitService.getUnitByManualId(manualId);
            return new ResponseEntity<>(units, HttpStatus.OK);
        } catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
