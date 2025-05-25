package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.ContentDTO;
import com.example.sciencekidbe.dto.ContentRequest;
import com.example.sciencekidbe.model.Information;

public interface IInformationService {
    Information addContent(ContentRequest contentRequest);

    ContentDTO modifyContent(Long contentId, String newText);

    void deleteContent(Long contentId);
}
