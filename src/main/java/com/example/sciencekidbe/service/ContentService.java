package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ContentDTO;
import com.example.sciencekidbe.model.Content;
import com.example.sciencekidbe.repository.ContentRepository;
import com.example.sciencekidbe.service.interfaces.IContentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentService implements IContentService {
    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository){
        this.contentRepository = contentRepository;
    }

    private ContentDTO convertToDTO(Content content){
        return new ContentDTO(
                content.getContentId(),
                content.getText()
        );
    }

    public List<ContentDTO> getContentByInformation(Long informationId){
        List<Content> contents = contentRepository.findByInformation_InformationId(informationId);

        if(contents.isEmpty()){
            throw new IllegalArgumentException("Nu exista continut!");
        }

        return contents.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}

