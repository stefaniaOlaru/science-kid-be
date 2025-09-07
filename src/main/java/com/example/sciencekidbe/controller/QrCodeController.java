package com.example.sciencekidbe.controller;

import com.example.sciencekidbe.dto.QrCodeRequest;
import com.example.sciencekidbe.service.QrCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/qrcode")
@CrossOrigin(origins = "*")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generateQrCode(
            @RequestPart("request") QrCodeRequest qrCodeRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try{
            qrCodeService.generateQrCode(qrCodeRequest, file);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
