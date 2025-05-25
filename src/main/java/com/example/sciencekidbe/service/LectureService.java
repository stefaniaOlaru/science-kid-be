package com.example.sciencekidbe.service;

import com.example.sciencekidbe.dto.LectureDTO;
import com.example.sciencekidbe.model.Lecture;
import com.example.sciencekidbe.repository.LectureRepository;
import com.example.sciencekidbe.service.interfaces.ILectureService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LectureService implements ILectureService {
    private final LectureRepository lectureRepository;

    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    private LectureDTO convertToDTO(Lecture lecture){
        return new LectureDTO(
                lecture.getLectureId(),
                lecture.getLectureName(),
                lecture.getUnit().getUnitId(),
                lecture.getObservation().getObservationId(),
                lecture.getApplication().getApplicationId(),
                lecture.getInformation().getInformationId()
        );
    }

    public List<LectureDTO> getLecturesByUnitId(Long unitId) {
        List<Lecture> lectures = lectureRepository.findByUnit_UnitId(unitId);

        if(lectures.isEmpty()){
            throw new IllegalArgumentException("Nu s-au gasit lecture pentru unitul respectiv");
        }

        return lectures.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
