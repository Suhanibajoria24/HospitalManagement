package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.Entity.TrainedIn;
import com.example.HospitalManagement.Entity.TrainedInId;
import com.example.HospitalManagement.Exception.CertificationNotFoundException;
import com.example.HospitalManagement.Repository.TrainedInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TrainedInController {

    @Autowired
    private TrainedInRepository trainedInRepository;

 @PatchMapping("/trainedIn/renew/{treatmentId}/{physicianId}")
public ResponseEntity<TrainedIn> renewCertification(
        @PathVariable Integer treatmentId,
        @PathVariable Integer physicianId,
        @RequestBody TrainedIn request) {

    TrainedInId id = new TrainedInId(physicianId, treatmentId);

    TrainedIn trainedIn = trainedInRepository.findById(id)
            .orElseThrow(() -> new CertificationNotFoundException(physicianId, treatmentId));

    // Only update certificationDate if provided
    if (request.getCertificationDate() != null) {
        trainedIn.setCertificationDate(request.getCertificationDate());
    }
    // Always update expiry
    trainedIn.setCertificationExpires(request.getCertificationExpires());

    return ResponseEntity.ok(trainedInRepository.save(trainedIn));
}
}