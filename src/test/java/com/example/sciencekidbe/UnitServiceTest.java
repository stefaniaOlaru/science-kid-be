package com.example.sciencekidbe;

import com.example.sciencekidbe.dto.UnitDTO;
import com.example.sciencekidbe.model.Unit;
import com.example.sciencekidbe.repository.UnitRepository;
import com.example.sciencekidbe.service.UnitService;
import org.junit.jupiter.api.BeforeEach;
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
public class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private UnitService unitService;

    private Unit unit;

    @BeforeEach
    public void setUp() {
        unit = new Unit();
        unit.setUnitId(1L);
        unit.setUnitName("Unit 1");
        unit.setDescription("Description of Unit 1");
    }

    @Test
    public void testGetUnitByManualId_Success() {
        when(unitRepository.findByManual_ManualId(1L)).thenReturn(List.of(unit));

        List<UnitDTO> result = unitService.getUnitByManualId(1L);

        assertEquals(1, result.size());
        UnitDTO dto = result.get(0);
        assertEquals(1L, dto.getUnitId());
        assertEquals("Unit 1", dto.getUnitName());
        assertEquals("Description of Unit 1", dto.getDescription());
    }

    @Test
    public void testGetUnitByManualId_NoUnitsFound() {
        when(unitRepository.findByManual_ManualId(1L)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                unitService.getUnitByManualId(1L));

        assertEquals("Nu s-au gasit unit-uri pt manualul specificat>", exception.getMessage());
    }
}

