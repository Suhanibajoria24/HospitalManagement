package com.example.HospitalManagement.Exception;

public class CertificationNotFoundException extends RuntimeException {
    public CertificationNotFoundException(Integer physicianId, Integer treatmentId) {
        super("No certification found for physician " + physicianId + 
              " and treatment " + treatmentId);
    }
}
