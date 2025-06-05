package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ObservationDTO;
import com.example.sciencekidbe.dto.ObservationImageDTO;
import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.model.Observation;
import com.example.sciencekidbe.model.ObservationImage;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.repository.ObservationRepository;
import io.micrometer.observation.ObservationTextPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObservationService {

    private final ObservationRepository observationRepository;
    private final FileService fileService;

    public ObservationService(ObservationRepository observationRepository, FileService fileService){
        this.observationRepository = observationRepository;
        this.fileService = fileService;
    }

    private ObservationImageDTO mapToImageDTO(ObservationImage image) {
        String urlWithSas = null;
        if (image.getImagePath() != null && !image.getImagePath().isEmpty()) {
            urlWithSas = fileService.generateUrlWithSasToken(image.getImagePath());
        }
        return new ObservationImageDTO(
                image.getImageId(),
                urlWithSas != null ? urlWithSas : image.getImagePath()
        );
    }


    private ObservationDTO convertToDTO(Observation observation) {
        List<ObservationImageDTO> imageDTOs = observation.getImages().stream()
                .map(this::mapToImageDTO)
                .collect(Collectors.toList());

        return new ObservationDTO(
                observation.getObservationId(),
                imageDTOs
        );
    }

    public ObservationDTO getObservationById(Long observationId){
        Observation observation = observationRepository.findById(observationId).orElse(null);

        if (observation == null){
            throw new IllegalArgumentException("Observația nu a fost gasita");
        }

        return convertToDTO(observation);
    }
}
