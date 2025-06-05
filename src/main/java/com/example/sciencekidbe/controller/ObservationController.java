package com.example.sciencekidbe.controller;


import com.example.sciencekidbe.dto.ObservationDTO;
import com.example.sciencekidbe.model.Observation;
import com.example.sciencekidbe.service.ObservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/observation")
@CrossOrigin("*")
public class ObservationController {
    private final ObservationService observationService;

    public ObservationController(ObservationService observationService){
        this.observationService = observationService;
    }

    @GetMapping("/{observationId}")
    public ResponseEntity<ObservationDTO> getObservationByLecture(@PathVariable Long observationId){
        try{
            ObservationDTO observation = observationService.getObservationById(observationId);
            return new ResponseEntity<>(observation, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
