package com.example.HospitalManagement.Projection;



import com.example.HospitalManagement.Entity.Physician;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "physicianSummary", types = { Physician.class })
public interface PhysicianSummaryProjection {
    
    // We only ask for ID, Name, and Position. 
    // Because we leave out getSsn(), Spring will completely hide it!
    Integer getEmployeeId();
    String getName();
    String getPosition();
}
