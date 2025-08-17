package com.example.sciencekidbe;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.sciencekidbe.dto.ObservationDTO;
import com.example.sciencekidbe.dto.ObservationImageDTO;
import com.example.sciencekidbe.dto.ObservationQuestionDTO;
import com.example.sciencekidbe.model.*;
import com.example.sciencekidbe.repository.ObservationRepository;
import com.example.sciencekidbe.service.FileService;
import com.example.sciencekidbe.service.ObservationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

class ObservationServiceTest {

    @Mock
    private ObservationRepository observationRepository;

    @Mock
    private FileService fileService;

    @InjectMocks
    private ObservationService observationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // =================== getObservationById ===================
    @Test
    void getObservationById_shouldReturnObservationDTOSuccessfully() {
        // Setup questions
        ObservationQuestion question1 = new ObservationQuestion();
        question1.setQuestionId(1L);
        question1.setQuestionContent("What is this?");
        question1.setAnswer("A flower");

        ObservationQuestion question2 = new ObservationQuestion();
        question2.setQuestionId(2L);
        question2.setQuestionContent("Color?");
        question2.setAnswer("Red");

        // Setup image with questions
        ObservationImage image = new ObservationImage();
        image.setImageId(10L);
        image.setImagePath("path/to/image.png");
        image.setQuestions(Arrays.asList(question1, question2));

        // Setup observation with images
        Observation observation = new Observation();
        observation.setObservationId(100L);
        observation.setImages(Collections.singletonList(image));
        observation.setAudioPath("path/to/audio.mp3");

        // Mock repository and file service
        when(observationRepository.findById(100L)).thenReturn(Optional.of(observation));
        when(fileService.generateUrlWithSasToken("path/to/image.png")).thenReturn("sas_image_url");
        when(fileService.generateUrlWithSasToken("path/to/audio.mp3")).thenReturn("sas_audio_url");

        ObservationDTO result = observationService.getObservationById(100L);

        assertEquals(100L, result.getObservationId());
        assertEquals("sas_audio_url", result.getAudioPath());
        assertEquals(1, result.getObservationImages().size());

        ObservationImageDTO imageDTO = result.getObservationImages().get(0);
        assertEquals(10L, imageDTO.getImageId());
        assertEquals("sas_image_url", imageDTO.getImagePath());
        assertEquals(2, imageDTO.getQuestions().size());

        ObservationQuestionDTO q1DTO = imageDTO.getQuestions().get(0);
        assertEquals(1L, q1DTO.getQuestionId());
        assertEquals("What is this?", q1DTO.getQuestionContent());
        assertEquals("A flower", q1DTO.getAnswer());
    }

    @Test
    void getObservationById_shouldReturnObservationDTOWithNullAudioOrImagePath() {
        ObservationImage image = new ObservationImage();
        image.setImageId(20L);
        image.setImagePath(null);
        image.setQuestions(Collections.emptyList());

        Observation observation = new Observation();
        observation.setObservationId(200L);
        observation.setImages(Collections.singletonList(image));
        observation.setAudioPath(null);

        when(observationRepository.findById(200L)).thenReturn(Optional.of(observation));

        ObservationDTO result = observationService.getObservationById(200L);

        assertEquals(200L, result.getObservationId());
        assertNull(result.getAudioPath());
        assertEquals(1, result.getObservationImages().size());
        assertNull(result.getObservationImages().get(0).getImagePath());
    }

    @Test
    void getObservationById_shouldThrowException_whenObservationNotFound() {
        when(observationRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> observationService.getObservationById(999L));
        assertTrue(ex.getMessage().contains("nu a fost gasita"));
    }
}

