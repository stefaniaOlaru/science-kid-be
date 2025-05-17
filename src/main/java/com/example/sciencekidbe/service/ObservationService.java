package com.example.sciencekidbe.service;

import com.example.sciencekidbe.model.Observation;
import com.example.sciencekidbe.repository.ObservationRepository;
import io.micrometer.observation.ObservationTextPublisher;
import org.springframework.stereotype.Service;

@Service
public class ObservationService {

    private final ObservationRepository observationRepository;

    public ObservationService(ObservationRepository observationRepository){
        this.observationRepository = observationRepository;
    }

    public Observation getObservationById(Long observationId){
        Observation observation = observationRepository.findById(observationId)
                .orElseThrow(() ->  new IllegalArgumentException("Observația nu a fost gasita"));

        return observation;
    }
}
