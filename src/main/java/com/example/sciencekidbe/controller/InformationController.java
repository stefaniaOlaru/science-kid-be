package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.ContentRequest;
import com.example.sciencekidbe.model.Information;
import com.example.sciencekidbe.model.Lecture;
import com.example.sciencekidbe.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/information")
@CrossOrigin(origins = "*")
public class InformationController{

    private final InformationService informationService;

    public InformationController(InformationService informationService){
        this.informationService = informationService;
    }

   @PostMapping("/add-content")
   public ResponseEntity<?> addContent(@RequestBody ContentRequest contentRequest){
        try{
            return ResponseEntity.ok(informationService.addContent(contentRequest));
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la adaugarea contentului.");
        }
   }

    @PutMapping("/modify-content/{contentId}")
    public ResponseEntity<?> modifyContent(
            @PathVariable Long contentId,
            @RequestBody ContentRequest contentRequest) {
        try {
            return ResponseEntity.ok(informationService.modifyContent(contentId, contentRequest.getText()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la modificarea conținutului.");
        }
    }

    @DeleteMapping("/remove-content/{contentId}")
    public ResponseEntity<?> removeContent(@PathVariable Long contentId) {
        try {
            return ResponseEntity.ok(informationService.removeContent(contentId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la ștergerea conținutului.");
        }
    }

    //daca e sa folosim acest api trebuie safacem un informationDTO pt afisare
    @GetMapping("/{informationId}")
    public ResponseEntity<Information> getInformtionById(@PathVariable Long informationId){
        try{
            Information information = informationService.getInformationById(informationId);
            return new ResponseEntity<>(information,HttpStatus.OK);
        } catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
