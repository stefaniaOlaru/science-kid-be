package com.example.sciencekidbe;

import com.example.sciencekidbe.dto.ContentDTO;
import com.example.sciencekidbe.model.Content;
import com.example.sciencekidbe.repository.ContentRepository;
import com.example.sciencekidbe.service.ContentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentService contentService;

    @Test
    void getContentByInformation_shouldReturnContentDTOs_whenContentExists() {
        Content content1 = new Content();
        content1.setContentId(1L);
        content1.setText("Content 1");

        Content content2 = new Content();
        content2.setContentId(2L);
        content2.setText("Content 2");

        when(contentRepository.findByInformation_InformationId(1L))
                .thenReturn(List.of(content1, content2));

        List<ContentDTO> result = contentService.getContentByInformation(1L);

        assertEquals(2, result.size());
        assertEquals("Content 1", result.get(0).getText());
        assertEquals("Content 2", result.get(1).getText());
    }

    @Test
    void getContentByInformation_shouldThrowException_whenNoContentFound() {
        when(contentRepository.findByInformation_InformationId(1L))
                .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                contentService.getContentByInformation(1L)
        );

        assertEquals("Nu exista continut!", exception.getMessage());
    }

    @Test
    void convertToDTO_shouldMapContentFieldsCorrectly() throws Exception {
        Content content = new Content();
        content.setContentId(1L);
        content.setText("Sample Text");

        Method convertToDTO = ContentService.class.getDeclaredMethod("convertToDTO", Content.class);
        convertToDTO.setAccessible(true);

        ContentDTO dto = (ContentDTO) convertToDTO.invoke(contentService, content);

        assertEquals(1L, dto.getContnId());
        assertEquals("Sample Text", dto.getText());
    }
}
