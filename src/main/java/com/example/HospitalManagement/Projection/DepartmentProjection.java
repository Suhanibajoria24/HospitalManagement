package com.example.HospitalManagement.Projection;



import com.example.HospitalManagement.Entity.Department;
import com.example.HospitalManagement.Entity.Physician;
import org.springframework.data.rest.core.config.Projection;

// The name "fullDepartment" is what we will use in the URL later!
@Projection(name = "fullDepartment", types = { Department.class })
public interface DepartmentProjection {
    
    // 1. Grab the basic department info
    Integer getDepartmentId();
    String getName();
    
    // 2. THE MAGIC LINE: This tells Spring to embed the full Physician object 
    // instead of just returning a URL link!
    Physician getHead(); 
}
