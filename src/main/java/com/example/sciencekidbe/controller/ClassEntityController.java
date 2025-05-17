package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.ClassEntityDTO;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.service.ClassEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*")
public class ClassEntityController {
    private final ClassEntityService classEntityService;

    public ClassEntityController(ClassEntityService classEntityService){
        this.classEntityService = classEntityService;
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllClasses(){
        List<String> classes = classEntityService.getAllClassNames();
        return new ResponseEntity<>(classes, HttpStatus.OK);
    }

    @GetMapping("/details/{className}")
    public ResponseEntity<ClassEntityDTO> getClassDetails(@PathVariable String className){
        ClassEntity classEntity = classEntityService.getClassDetailsByName(className);

        if (classEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ClassEntityDTO classDetails = classEntityService.convertToClassEntityDTO(classEntity);
        return new ResponseEntity<>(classDetails, HttpStatus.OK);
    }
}
