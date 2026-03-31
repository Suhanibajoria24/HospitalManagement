package com.example.HospitalManagement.Event;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataIntegrityViolationException;
import com.example.HospitalManagement.Entity.TrainedIn;
import com.example.HospitalManagement.Entity.TrainedInId;
import com.example.HospitalManagement.Repository.TrainedInRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@RepositoryEventHandler
public class TrainedInEventHandler {

    @Autowired
    private TrainedInRepository trainedInRepository;

    @HandleBeforeCreate
    public void handleBeforeCreate(TrainedIn trainedIn) {
        if (trainedInRepository.existsById(
                new TrainedInId(trainedIn.getPhysician(), trainedIn.getTreatment()))) {
            throw new DataIntegrityViolationException(
                "duplicate entry for physician=" + trainedIn.getPhysician() +
                " treatment=" + trainedIn.getTreatment());
        }
    }
}
