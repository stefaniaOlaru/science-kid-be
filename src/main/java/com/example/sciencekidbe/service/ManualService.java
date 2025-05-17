package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.ManualDTO;
import com.example.sciencekidbe.dto.StudentDTO;
import com.example.sciencekidbe.model.ClassEntity;
import com.example.sciencekidbe.model.Manual;
import com.example.sciencekidbe.model.Student;
import com.example.sciencekidbe.repository.ClassEntityRepository;
import com.example.sciencekidbe.repository.ManualRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ManualService {

    private final ManualRepository manualRepository;
    private final ClassEntityRepository classEntityRepository;

    public ManualService(ManualRepository manualRepository, ClassEntityRepository classEntityRepository){
        this.manualRepository = manualRepository;
        this.classEntityRepository = classEntityRepository;
    }

    public Manual addManual(ManualDTO manualDTO){
        List<ClassEntity> classEntities = manualDTO.getClassNames().stream()
                .map(classEntityRepository::findByClassName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (classEntities.isEmpty()){
            throw new IllegalArgumentException("No valid classes found for the provided namesd.");
        }

        Manual manual = new Manual();
        manual.setManualName(manualDTO.getManualName());
        manual.setPublisher(manualDTO.getPublisher());
        manual.setClassEntities(classEntities);

        return manualRepository.save(manual);
    }

    private ManualDTO convertToDTO(Manual manual){
        return new ManualDTO(
                manual.getManualId(),
                manual.getManualName(),
                manual.getPublisher(),
                manual.getClassEntities().stream()
                        .map(ClassEntity::getClassName)
                        .collect(Collectors.toList())
        );
    }

    public List<ManualDTO> getAllManual(){
        List<Manual> manuals = manualRepository.findAll();

        if(manuals.isEmpty()){
            throw new IllegalArgumentException("Nu s-au gasit manuale.");
        }

        return manuals.stream().map(this::convertToDTO).collect(Collectors.toList());
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

        List<ClassEntity> updatedClassEntities = manualDTO.getClassNames().stream()
                .map(classEntityRepository::findByClassName)
                .filter(Objects::nonNull)
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
            manuals = manualRepository.findAll().stream()
                    .filter(manual -> manual.getManualName().equals(manualName))
                    .filter(manual -> manual.getPublisher().equals(publisher))
                    .filter(manual -> manual.getClassEntities().stream()
                            .anyMatch(classEntity -> classEntity.getClassName().equals(className)))
                    .collect(Collectors.toList());

        } else if(manualName != null && publisher != null){
                manuals = manualRepository.findAll().stream()
                    .filter(manual -> manual.getManualName().equals(manualName))
                    .filter(manual -> manual.getPublisher().equals(publisher))
                    .collect(Collectors.toList());

        } else if( publisher != null && className != null){
            manuals = manualRepository.findAll().stream()
                    .filter(manual -> manual.getPublisher().equals(publisher))
                    .filter(manual -> manual.getClassEntities().stream()
                            .anyMatch(classEntity -> classEntity.getClassName().equals(className)))
                    .collect(Collectors.toList());

        } else if(manualName != null && className != null){
            manuals = manualRepository.findAll().stream()
                    .filter(manual -> manual.getManualName().equals(manualName))
                    .filter(manual -> manual.getClassEntities().stream()
                            .anyMatch(classEntity -> classEntity.getClassName().equals(className)))
                    .collect(Collectors.toList());
        } else if(manualName != null){
            manuals = manualRepository.findByManualName(manualName);

        } else if(publisher != null){
            manuals = manualRepository.findByPublisher(publisher);

        } else if(className != null){
            manuals = manualRepository.findByClassEntities_ClassName(className);

        } else {
            manuals = manualRepository.findAll();
        }

        return manuals.stream().map(this::convertToDTO).collect(Collectors.toList());
   }
}
