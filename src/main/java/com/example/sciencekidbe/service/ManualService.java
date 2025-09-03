package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ManualDTO;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.Manual;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.ManualRepository;
import com.example.sciencekidbe.service.interfaces.IFileService;
import com.example.sciencekidbe.service.interfaces.IManualService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManualService implements IManualService {

    private final ManualRepository manualRepository;
    private final ClassEntityRepository classEntityRepository;
    private final IFileService fileService;

    public ManualService(ManualRepository manualRepository, ClassEntityRepository classEntityRepository, IFileService fileService){
        this.manualRepository = manualRepository;
        this.classEntityRepository = classEntityRepository;
        this.fileService = fileService;
    }

    public Manual addManual(ManualDTO manualDTO){
        List<ClassEntity> classEntities = new ArrayList<>();

        for (Long classId : manualDTO.getClassEntityIds()) {
            ClassEntity classEntity = classEntityRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("Clasa cu ID-ul " + classId + " nu a fost găsită."));
            classEntities.add(classEntity);
        }

        Manual manual = new Manual();
        manual.setManualName(manualDTO.getManualName());
        manual.setPublisher(manualDTO.getPublisher());
        manual.setClassEntities(classEntities);

        return manualRepository.save(manual);
    }

    private ManualDTO convertToDTO(Manual manual){
        List<String> classNames = manual.getClassEntities().stream()
                .map(ClassEntity::getClassName)
                .collect(Collectors.toList());

        List<Long> classIds = manual.getClassEntities()
                .stream()
                .map(ClassEntity::getClassId)
                .collect(Collectors.toList());

        return new ManualDTO(
                manual.getManualId(),
                manual.getManualName(),
                manual.getPublisher(),
                manual.getPdfPath(),
                classNames,
                classIds
        );
    }

    public List<ManualDTO> getAllManual(){
        List<Manual> manuals = manualRepository.findAll();

        if(manuals.isEmpty()){
            throw new IllegalArgumentException("Nu s-au gasit manuale.");
        }

        return manuals.stream()
                .map(manual -> {
                    ManualDTO dto = convertToDTO(manual);

                    String originalPath = manual.getPdfPath();
                    if (originalPath != null && !originalPath.isEmpty()) {
                        String urlWithSas = fileService.generateUrlWithSasToken(originalPath);
                        dto.setPdfPath(urlWithSas);
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteManual (Long manualId){
        Manual manual = manualRepository.findById(manualId)
                .orElseThrow(() -> new IllegalArgumentException("Manualul cu id-ul specificat nu exista"));

        manualRepository.delete(manual);
    }

   public Manual updateManual(ManualDTO manualDTO){
        Manual existingManual = manualRepository.findById(manualDTO.getManualId())
                .orElseThrow(() -> new IllegalArgumentException("Manualul nu exista"));

        List<ClassEntity> currentClassEntities = existingManual.getClassEntities();

        List<ClassEntity> updatedClassEntities = manualDTO.getClassEntityIds().stream()
                .map(classEntityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        List<ClassEntity> classesToAdd = updatedClassEntities.stream()
                .filter(classEntity -> !currentClassEntities.contains(classEntity))
                .toList();

        currentClassEntities.addAll(classesToAdd);

        List<ClassEntity> classesToRemove = currentClassEntities.stream()
                .filter(classEntity -> !updatedClassEntities.contains(classEntity))
                .toList();

        currentClassEntities.removeAll(classesToRemove);

        existingManual.setManualName(manualDTO.getManualName());
        existingManual.setPublisher(manualDTO.getPublisher());
        existingManual.setClassEntities(currentClassEntities);

        return manualRepository.save(existingManual);
   }

   public List<ManualDTO> filterManuals(String manualName, String publisher, String className){
        List<Manual> manuals;

        if(manualName != null && publisher != null && className != null){
            String manualNameLower = manualName.toLowerCase();
            String publisherLower = publisher.toLowerCase();
            String classNameLower = className.toLowerCase();

            manuals = manualRepository.findAll().stream()
                    .filter(manual -> manual.getManualName().toLowerCase().contains(manualNameLower))
                    .filter(manual -> manual.getPublisher().toLowerCase().contains(publisherLower))
                    .filter(manual -> manual.getClassEntities().stream()
                            .anyMatch(classEntity -> classEntity.getClassName().toLowerCase().contains(classNameLower)))
                    .collect(Collectors.toList());

        } else if(manualName != null && publisher != null){
            String manualNameLower = manualName.toLowerCase();
            String publisherLower = publisher.toLowerCase();

            manuals = manualRepository.findAll().stream()
                    .filter(manual -> manual.getManualName().toLowerCase().contains(manualNameLower))
                    .filter(manual -> manual.getPublisher().toLowerCase().contains(publisherLower))
                    .collect(Collectors.toList());

        } else if( publisher != null && className != null){
            String publisherLower = publisher.toLowerCase();
            String classNameLower = className.toLowerCase();

            manuals = manualRepository.findAll().stream()
                    .filter(manual -> manual.getPublisher().toLowerCase().contains(publisherLower))
                    .filter(manual -> manual.getClassEntities().stream()
                            .anyMatch(classEntity -> classEntity.getClassName().toLowerCase().contains(classNameLower)))
                    .collect(Collectors.toList());

        } else if(manualName != null && className != null){
            String manualNameLower = manualName.toLowerCase();
            String classNameLower = className.toLowerCase();

            manuals = manualRepository.findAll().stream()
                    .filter(manual -> manual.getManualName().toLowerCase().contains(manualNameLower))
                    .filter(manual -> manual.getClassEntities().stream()
                            .anyMatch(classEntity -> classEntity.getClassName().toLowerCase().contains(classNameLower)))
                    .collect(Collectors.toList());
        } else if(manualName != null){
            String manualNameLower = manualName.toLowerCase();
            manuals = manualRepository.findAll().stream()
                    .filter(manual -> manual.getManualName() != null &&
                            manual.getManualName().toLowerCase().contains(manualNameLower))
                    .collect(Collectors.toList());


        } else if(publisher != null){
            String publisherLower = publisher.toLowerCase();
            manuals = manualRepository.findAll().stream()
                    .filter(manual -> manual.getPublisher() != null &&
                            manual.getPublisher().toLowerCase().contains(publisherLower))
                    .collect(Collectors.toList());

        } else if(className != null){
            String classNameLower = className.toLowerCase();
            manuals = manualRepository.findAll().stream()
                    .filter(manual -> manual.getClassEntities().stream()
                            .anyMatch(classEntity -> classEntity.getClassName().toLowerCase().contains(classNameLower)))
                    .collect(Collectors.toList());

        } else {
            manuals = manualRepository.findAll();
        }

        return manuals.stream().map(this::convertToDTO).collect(Collectors.toList());
   }
}
