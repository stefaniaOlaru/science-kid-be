package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.enums.ContainerName;
import com.example.sciencekidbe.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static com.example.sciencekidbe.helpers.FileHelpers.mapStringToContainerName;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/image")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @PostMapping("upload/{containerName}/{entityId}")
    public ResponseEntity<String> uploadImage(
            @PathVariable String containerName,
            @PathVariable Long entityId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            ContainerName enumContainerName = mapStringToContainerName(containerName);

            // Save the uploaded file temporarily to disk
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            String blobUrl = fileService.uploadFileToBlob(entityId, enumContainerName, tempFile.getAbsolutePath());

            // Clean up the temporary file
            tempFile.delete();

            // Return blob URL
            return ResponseEntity.ok(blobUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Eroare la upload: " + e.getMessage());
        }
    }
}
