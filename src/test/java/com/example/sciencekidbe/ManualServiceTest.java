package com.example.sciencekidbe;

import com.example.sciencekidbe.dto.ManualDTO;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.Manual;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.ManualRepository;
import com.example.sciencekidbe.service.ManualService;
import com.example.sciencekidbe.service.interfaces.IFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManualServiceTest {

    @Mock
    private ManualRepository manualRepository;

    @Mock
    private ClassEntityRepository classEntityRepository;

    @Mock
    private IFileService fileService;

    @InjectMocks
    private ManualService manualService;

    @Test
    void addManual_shouldSaveManualSuccessfully() {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassId(1L);
        when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));

        ManualDTO manualDTO = new ManualDTO();
        manualDTO.setManualName("Math");
        manualDTO.setPublisher("Publisher");
        manualDTO.setClassEntityIds(List.of(1L));

        Manual savedManual = new Manual();
        savedManual.setManualId(10L);
        savedManual.setManualName("Math");
        savedManual.setPublisher("Publisher");
        savedManual.setClassEntities(List.of(classEntity));
        when(manualRepository.save(any(Manual.class))).thenReturn(savedManual);

        Manual result = manualService.addManual(manualDTO);

        assertEquals(10L, result.getManualId());
        assertEquals("Math", result.getManualName());
        assertEquals(1, result.getClassEntities().size());
    }

    @Test
    void getAllManual_shouldReturnManualDTOsWithSasUrls() {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassId(1L);
        classEntity.setClassName("Class A");

        Manual manual = new Manual();
        manual.setManualId(10L);
        manual.setManualName("Math");
        manual.setPublisher("Publisher");
        manual.setPdfPath("path/to/pdf");
        manual.setClassEntities(List.of(classEntity));

        when(manualRepository.findAll()).thenReturn(List.of(manual));
        when(fileService.generateUrlWithSasToken("path/to/pdf")).thenReturn("sasUrl");

        List<ManualDTO> dtos = manualService.getAllManual();

        assertEquals(1, dtos.size());
        assertEquals("sasUrl", dtos.get(0).getPdfPath());
    }

    @Test
    void deleteManual_shouldDeleteManualSuccessfully() {
        Manual manual = new Manual();
        manual.setManualId(10L);
        when(manualRepository.findById(10L)).thenReturn(Optional.of(manual));

        manualService.deleteManual(10L);

        verify(manualRepository, times(1)).delete(manual);
    }

    @Test
    void updateManual_shouldUpdateExistingManual() {
        ClassEntity classEntity1 = new ClassEntity();
        classEntity1.setClassId(1L);

        ClassEntity classEntity2 = new ClassEntity();
        classEntity2.setClassId(2L);

        Manual existingManual = new Manual();
        existingManual.setManualId(10L);
        existingManual.setManualName("Old Name");
        existingManual.setPublisher("Old Publisher");
        existingManual.setClassEntities(new ArrayList<>(List.of(classEntity1)));

        when(manualRepository.findById(10L)).thenReturn(Optional.of(existingManual));
        when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity1));
        when(classEntityRepository.findById(2L)).thenReturn(Optional.of(classEntity2));
        when(manualRepository.save(existingManual)).thenReturn(existingManual);

        ManualDTO manualDTO = new ManualDTO();
        manualDTO.setManualId(10L);
        manualDTO.setManualName("New Name");
        manualDTO.setPublisher("New Publisher");
        manualDTO.setClassEntityIds(List.of(1L,2L));

        Manual updatedManual = manualService.updateManual(manualDTO);

        assertEquals("New Name", updatedManual.getManualName());
        assertEquals("New Publisher", updatedManual.getPublisher());
        assertEquals(2, updatedManual.getClassEntities().size());
    }

    @Test
    void filterManuals_shouldReturnFilteredManuals() {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassName("Class A");

        Manual manual = new Manual();
        manual.setManualName("Math");
        manual.setPublisher("Publisher");
        manual.setClassEntities(List.of(classEntity));

        when(manualRepository.findAll()).thenReturn(List.of(manual));

        List<ManualDTO> result = manualService.filterManuals("Math", "Publisher", "Class A");

        assertEquals(1, result.size());
        assertEquals("Math", result.get(0).getManualName());
    }

    @Test
    void addManual_shouldThrowException_whenClassNotFound() {
        ManualDTO manualDTO = new ManualDTO();
        manualDTO.setManualName("Math");
        manualDTO.setPublisher("Publisher");
        manualDTO.setClassEntityIds(List.of(1L));

        when(classEntityRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manualService.addManual(manualDTO);
        });

        assertEquals("Clasa cu ID-ul 1 nu a fost găsită.", exception.getMessage());
    }

    @Test
    void getAllManual_shouldThrowException_whenNoManualsFound() {
        when(manualRepository.findAll()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manualService.getAllManual();
        });

        assertEquals("Nu s-au gasit manuale.", exception.getMessage());
    }

    @Test
    void deleteManual_shouldThrowException_whenManualNotFound() {
        when(manualRepository.findById(10L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manualService.deleteManual(10L);
        });

        assertEquals("Manualul cu id-ul specificat nu exista", exception.getMessage());
    }

    @Test
    void updateManual_shouldThrowException_whenManualNotFound() {
        ManualDTO manualDTO = new ManualDTO();
        manualDTO.setManualId(10L);

        when(manualRepository.findById(10L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manualService.updateManual(manualDTO);
        });

        assertEquals("Manualul nu exista", exception.getMessage());
    }
}

