package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.LectureDTO;
import com.example.sciencekidbe.model.Lecture;
import com.example.sciencekidbe.model.QrCode;
import com.example.sciencekidbe.repository.LectureRepository;
import com.example.sciencekidbe.service.interfaces.ILectureService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LectureService implements ILectureService {
    private final LectureRepository lectureRepository;
    private final FileService fileService;

    public LectureService(LectureRepository lectureRepository,
                          FileService fileService) {
        this.lectureRepository = lectureRepository;
        this.fileService = fileService;
    }

    private LectureDTO convertToDTO(Lecture lecture) {
        List<String> qrPathsWithSas = Optional.ofNullable(lecture.getQrCodes())
                .orElse(Collections.emptyList())
                .stream()
                .map(QrCode::getPath)
                .filter(Objects::nonNull)
                .distinct()
                .map(fileService::generateUrlWithSasToken) // attach SAS token
                .toList();

        return new LectureDTO(
                lecture.getLectureId(),
                lecture.getLectureName(),
                lecture.getUnit().getUnitId(),
                lecture.getObservation().getObservationId(),
                lecture.getApplication().getApplicationId(),
                lecture.getInformation().getInformationId(),
                qrPathsWithSas
        );
    }


    public List<LectureDTO> getLecturesByUnitId(Long unitId) {
        List<Lecture> lectures = lectureRepository.findByUnit_UnitId(unitId);

        if(lectures.isEmpty()){
            throw new IllegalArgumentException("Nu s-au gasit lecture pentru unitul respectiv");
        }

        return lectures.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<String> getQRCodeByLectureId(Long lectureId){
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Nu exista lectia"));

        LectureDTO lectureDTO = convertToDTO(lecture);

        return lectureDTO.getQrCodes();
    }

    public LectureDTO getLectureById(Long lectureId)
    {
        Lecture lecture  = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Nu exista lectia cu id-ul dat"));

        return convertToDTO(lecture);
    }
}
