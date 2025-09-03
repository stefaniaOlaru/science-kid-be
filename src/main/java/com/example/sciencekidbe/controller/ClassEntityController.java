package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.ClassEntityDTO;
import com.example.sciencekidbe.dto.ClassSummaryDTO;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.service.ClassEntityService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESOR')")
    @GetMapping("/all")
    public ResponseEntity<List<ClassSummaryDTO>> getAllClasses(){
        List<ClassSummaryDTO> classes = classEntityService.getAllClasses();
        return new ResponseEntity<>(classes, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESOR')")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @GetMapping("/{classId}/students/csv")
    public ResponseEntity<Resource> downloadStudentsCsv(@PathVariable Long classId) {
        try{
            Resource resource = classEntityService.downloadClassCsvByClassId(classId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);
        } catch (IllegalArgumentException ex)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }



    }
}
