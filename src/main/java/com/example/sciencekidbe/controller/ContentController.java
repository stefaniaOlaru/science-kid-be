package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.ContentDTO;
import com.example.sciencekidbe.model.Content;
import com.example.sciencekidbe.service.ContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/content")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService){
        this.contentService = contentService;
    }

    @GetMapping("/by-information/{informationId}")
    public ResponseEntity<List<ContentDTO>> getContentsByInformation(@PathVariable Long informationId){
        try{
            List<ContentDTO> contents = contentService.getContentByInformation(informationId);
            return ResponseEntity.ok(contents);
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(null);
        }
    }
}
