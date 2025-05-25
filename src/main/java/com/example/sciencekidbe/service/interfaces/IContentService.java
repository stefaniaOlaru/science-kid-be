package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.ContentDTO;

import java.util.List;

public interface IContentService {
    List<ContentDTO> getContentByInformation(Long informationId);
}
