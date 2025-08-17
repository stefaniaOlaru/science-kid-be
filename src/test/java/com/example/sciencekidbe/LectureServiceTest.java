package com.example.sciencekidbe;

import com.example.sciencekidbe.dto.LectureDTO;
import com.example.sciencekidbe.model.*;
import com.example.sciencekidbe.repository.LectureRepository;
import com.example.sciencekidbe.service.LectureService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectureServiceTest {

    @Mock
    private LectureRepository lectureRepository;

    @InjectMocks
    private LectureService lectureService;

    @Test
    void getLecturesByUnitId_shouldReturnLectureDTOs_whenLecturesExist() {
        Unit unit = new Unit();
        unit.setUnitId(1L);

        Observation observation = new Observation();
        observation.setObservationId(2L);

        Application application = new Application();
        application.setApplicationId(3L);

        Information information = new Information();
        information.setInformationId(4L);

        Lecture lecture = new Lecture();
        lecture.setLectureId(10L);
        lecture.setLectureName("Lecture 1");
        lecture.setUnit(unit);
        lecture.setObservation(observation);
        lecture.setApplication(application);
        lecture.setInformation(information);

        when(lectureRepository.findByUnit_UnitId(1L)).thenReturn(List.of(lecture));

        List<LectureDTO> result = lectureService.getLecturesByUnitId(1L);

        assertEquals(1, result.size());
        LectureDTO dto = result.get(0);
        assertEquals(10L, dto.getLectureId());
        assertEquals("Lecture 1", dto.getLectureName());
        assertEquals(1L, dto.getUnitId());
        assertEquals(2L, dto.getObservationId());
        assertEquals(3L, dto.getApplicationId());
        assertEquals(4L, dto.getInformationId());
    }

    @Test
    void getLecturesByUnitId_shouldThrowException_whenNoLecturesFound() {
        when(lectureRepository.findByUnit_UnitId(1L)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                lectureService.getLecturesByUnitId(1L)
        );

        assertEquals("Nu s-au gasit lecture pentru unitul respectiv", exception.getMessage());
    }
}

