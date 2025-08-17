package com.example.sciencekidbe;

import com.example.sciencekidbe.dto.ContentDTO;
import com.example.sciencekidbe.dto.ContentRequest;
import com.example.sciencekidbe.model.Content;
import com.example.sciencekidbe.model.Information;
import com.example.sciencekidbe.repository.ContentRepository;
import com.example.sciencekidbe.repository.InformationRepository;
import com.example.sciencekidbe.service.InformationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InformationServiceTest {

    @Mock
    private InformationRepository informationRepository;

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private InformationService informationService;

    @Test
    void addContent_shouldAddContentToInformation() {
        Information info = new Information();
        info.setInformationId(1L);
        info.setContents(new ArrayList<>());

        ContentRequest request = new ContentRequest();
        request.setInformationId(1L);
        request.setText("New content");

        when(informationRepository.findById(1L)).thenReturn(Optional.of(info));
        when(contentRepository.save(any(Content.class))).thenAnswer(i -> i.getArgument(0));
        when(informationRepository.save(any(Information.class))).thenAnswer(i -> i.getArgument(0));

        Information result = informationService.addContent(request);

        assertEquals(1, result.getContents().size());
        assertEquals("New content", result.getContents().get(0).getText());
    }

    @Test
    void addContent_shouldThrowException_whenInformationNotFound() {
        ContentRequest request = new ContentRequest();
        request.setInformationId(1L);
        request.setText("New content");

        when(informationRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                informationService.addContent(request)
        );

        assertEquals("Nu s-a gasit Informatia dorita", exception.getMessage());
    }

    @Test
    void modifyContent_shouldUpdateText() {
        Content content = new Content();
        content.setContentId(1L);
        content.setText("Old text");

        when(contentRepository.findById(1L)).thenReturn(Optional.of(content));
        when(contentRepository.save(any(Content.class))).thenAnswer(i -> i.getArgument(0));

        ContentDTO result = informationService.modifyContent(1L, "Updated text");

        assertEquals(1L, result.getContnId());
        assertEquals("Updated text", result.getText());
    }

    @Test
    void modifyContent_shouldThrowException_whenContentNotFound() {
        when(contentRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                informationService.modifyContent(1L, "Updated text")
        );

        assertEquals("Nu s-a gasit contentul", exception.getMessage());
    }

    @Test
    void deleteContent_shouldRemoveContentFromInformation() {
        Information info = new Information();
        info.setInformationId(1L);
        Content content = new Content();
        content.setContentId(1L);
        content.setInformation(info);
        info.setContents(new ArrayList<>(List.of(content)));

        when(contentRepository.findById(1L)).thenReturn(Optional.of(content));

        informationService.deleteContent(1L);

        assertFalse(info.getContents().contains(content));
        verify(contentRepository).delete(content);
    }

    @Test
    void deleteContent_shouldThrowException_whenContentNotFound() {
        when(contentRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                informationService.deleteContent(1L)
        );

        assertEquals("Nu s-a gasit contentul", exception.getMessage());
    }
}

