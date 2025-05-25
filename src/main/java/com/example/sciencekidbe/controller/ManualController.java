package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.ManualDTO;
import com.example.sciencekidbe.model.Manual;
import com.example.sciencekidbe.service.ManualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manual")
@CrossOrigin(origins = "*")
public class ManualController {

    private final ManualService manualService;

    @Autowired
    public  ManualController(ManualService manualService){
        this.manualService= manualService;
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateManual(@RequestBody ManualDTO manualDTO){
        try{
            manualService.updateManual(manualDTO);
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addManual(@RequestBody ManualDTO manualDTO){
        try{
            Manual newManual = manualService.addManual(manualDTO);

            return new ResponseEntity<>(newManual, HttpStatus.CREATED);
        } catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<ManualDTO>> getAllManual(){
        try{
            List<ManualDTO> manuals = manualService.getAllManual();

            return new ResponseEntity<>(manuals,HttpStatus.OK);
        } catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{manualId}")
    public ResponseEntity<Void> deleteManual(@PathVariable Long manualId){
        try{
            manualService.deleteManual(manualId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ManualDTO>> filterManuals(
            @RequestParam(required = false) String manualName,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String className){
        try{
            List<ManualDTO> filteredManuals = manualService.filterManuals(manualName, publisher, className);
            return ResponseEntity.ok(filteredManuals);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
