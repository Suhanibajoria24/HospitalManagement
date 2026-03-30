package com.example.HospitalManagement.Projection;



import com.example.HospitalManagement.Entity.AffiliatedWith;
import com.example.HospitalManagement.Entity.Department;
import com.example.HospitalManagement.Entity.Physician;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "fullAffiliation", types = { AffiliatedWith.class })
public interface AffiliationProjection {
    
    // Grab the basic boolean field
    Boolean getPrimaryAffiliation();
    
    // THE MAGIC LINES: Tell Spring to embed the full objects, not just links!
    Physician getPhysicianEntity();
    Department getDepartmentEntity();
}
