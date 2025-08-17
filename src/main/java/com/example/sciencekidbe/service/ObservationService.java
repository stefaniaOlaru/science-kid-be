package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ObservationDTO;
import com.example.sciencekidbe.dto.ObservationImageDTO;
import com.example.sciencekidbe.dto.ObservationQuestionDTO;
import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.model.Observation;
import com.example.sciencekidbe.model.ObservationImage;
import com.example.sciencekidbe.model.ObservationQuestion;
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

    private ObservationQuestionDTO mapToQuestionDTO(ObservationQuestion question){
        return new ObservationQuestionDTO(
                question.getQuestionId(),
                question.getQuestionContent(),
                question.getAnswer()
        );
    }


    private ObservationImageDTO mapToImageDTO(ObservationImage image) {
        String urlWithSas = null;
        if (image.getImagePath() != null && !image.getImagePath().isEmpty()) {
            urlWithSas = fileService.generateUrlWithSasToken(image.getImagePath());
        }

        List<ObservationQuestionDTO> questionDTOS = image.getQuestions().stream()
                .map(this::mapToQuestionDTO)
                .toList();

        return new ObservationImageDTO(
                image.getImageId(),
                urlWithSas != null ? urlWithSas : image.getImagePath(),
                questionDTOS
        );
    }


    private ObservationDTO convertToDTO(Observation observation) {
        List<ObservationImageDTO> imageDTOs = observation.getImages().stream()
                .map(this::mapToImageDTO)
                .collect(Collectors.toList());

        String audioPathWithSas = null;
        if (observation.getAudioPath() != null && !observation.getAudioPath().isEmpty()) {
            audioPathWithSas = fileService.generateUrlWithSasToken(observation.getAudioPath());
        }

        return new ObservationDTO(
                observation.getObservationId(),
                imageDTOs,
                audioPathWithSas
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
