package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.LectureDTO;
import com.example.sciencekidbe.dto.LectureDetailsDTO;
import com.example.sciencekidbe.model.Lecture;
import com.example.sciencekidbe.service.LectureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecture")
@CrossOrigin(origins = "*")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'PROFESOR')")
    @GetMapping("/unit/{unitId}")
    public ResponseEntity<List<LectureDTO>> getLecturesByUnit(@PathVariable Long unitId) {
        try{
            List<LectureDTO> lectures = lectureService.getLecturesByUnitId(unitId);
            return new ResponseEntity<>(lectures,HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
}
