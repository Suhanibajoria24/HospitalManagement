package com.example.HospitalManagement.Projection;

import com.example.HospitalManagement.Entity.Nurse;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "nurseView", types = Nurse.class)
public interface NurseProjection {

    String getName();
    String getPosition();
    boolean getRegistered();
    String getAvailability();
}