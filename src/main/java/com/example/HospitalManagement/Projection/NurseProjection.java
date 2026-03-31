package com.example.HospitalManagement.Projection;

import com.example.HospitalManagement.Entity.Nurse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "nurseView", types = Nurse.class)
public interface NurseProjection {

    String getName();
    String getPosition();
    boolean getRegistered();
    @Value("#{target.appointments != null && !target.appointments.isEmpty() ? 'BUSY (APPOINTMENT)' : (target.onCallSchedules != null && !target.onCallSchedules.isEmpty() ? 'BUSY (ON_CALL)' : 'AVAILABLE')}")
    String getAvailability();
}