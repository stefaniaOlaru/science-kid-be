package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.enums.ContainerName;

public interface IFileService {
    String uploadFileToBlob(Long entityId, ContainerName containerName, String filePath);
    String generateUrlWithSasToken(String blobUrl);
}
