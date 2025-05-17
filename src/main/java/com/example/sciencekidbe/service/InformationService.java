package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ContentDTO;
import com.example.sciencekidbe.dto.ContentRequest;
import com.example.sciencekidbe.model.Content;
import com.example.sciencekidbe.model.Information;
import com.example.sciencekidbe.model.Lecture;
import com.example.sciencekidbe.repository.ContentRepository;
import com.example.sciencekidbe.repository.InformationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InformationService {

    private final InformationRepository informationRepository;
    private final ContentRepository contentRepository;

    public InformationService(InformationRepository informationRepository, ContentRepository contentRepository){
        this.informationRepository = informationRepository;
        this.contentRepository = contentRepository;
    }

    public Information addContent(ContentRequest contentRequest){
        Information information = informationRepository.findById(contentRequest.getInformationId())
                .orElseThrow(() -> new IllegalArgumentException("Nu s-a gasit Informatia dorita"));

        Content content= new Content();
        content.setText(contentRequest.getText());
        content.setInformation(information);
        contentRepository.save(content);

        information.getContents().add(content);

        return informationRepository.save(information);
    }

    public ContentDTO modifyContent(Long contentId, String newText){
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Nu s-a gasit contentul"));

        content.setText(newText);
        Content saved = contentRepository.save(content);

        return new ContentDTO(saved.getContentId(), saved.getText());
    }

    public Information removeContent(Long contentId){
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Nu s-a gasit contentul"));

        Information information = content.getInformation();
        information.getContents().remove(content);
        contentRepository.delete(content);

        return information;
    }

    public Information getInformationById(Long informationId){
        Information information = informationRepository.findById(informationId)
                .orElseThrow(() -> new IllegalArgumentException("Nu s-a gasit information"));

        return information;
    }
}
