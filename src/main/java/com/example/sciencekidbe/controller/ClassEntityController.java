package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.ClassEntityDTO;
import com.example.sciencekidbe.dto.ClassSummaryDTO;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.service.ClassEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*")
public class ClassEntityController {
    private final ClassEntityService classEntityService;

    public ClassEntityController(ClassEntityService classEntityService){
        this.classEntityService = classEntityService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClassSummaryDTO>> getAllClasses(){
        List<ClassSummaryDTO> classes = classEntityService.getAllClasses();
        return new ResponseEntity<>(classes, HttpStatus.OK);
    }

    @GetMapping("/details/{classId}")
    public ResponseEntity<ClassEntityDTO> getClassDetails(@PathVariable Long classId){
        Optional<ClassEntity> classEntityOpt = classEntityService.getClassDetailsByClassId(classId);

        if (classEntityOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            ClassEntity classEntity = classEntityOpt.get();
            ClassEntityDTO classDetails = classEntityService.convertToClassEntityDTO(classEntity);
            return new ResponseEntity<>(classDetails, HttpStatus.OK);
        }
    }
}
