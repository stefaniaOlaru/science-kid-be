package com.example.sciencekidbe.service.interfaces;

import com.example.sciencekidbe.dto.ManualDTO;
import com.example.sciencekidbe.model.Manual;

import java.util.List;

public interface IManualService {
    Manual addManual(ManualDTO manualDTO);

    List<ManualDTO> getAllManual();

    void deleteManual (Long manualId);

    Manual updateManual(ManualDTO manualDTO);

    List<ManualDTO> filterManuals(String manualName, String publisher, String className);
}
