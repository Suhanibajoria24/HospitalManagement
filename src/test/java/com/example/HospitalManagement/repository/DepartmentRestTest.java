package com.example.HospitalManagement.repository;



import com.example.HospitalManagement.Entity.Department;
import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.DepartmentRepository;
import com.example.HospitalManagement.Repository.PhysicianRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Protects your real database
public class DepartmentRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartmentRepository departmentRepo;

    @Autowired
    private PhysicianRepository physicianRepo;

    // --- HELPER METHOD ---
    private Physician createTestPhysician(int id) {
        Physician p = new Physician();
        p.setEmployeeId(id);
        p.setName("Dr. Test Head " + id);
        p.setPosition("Chief");
        p.setSsn(id * 100); 
        return physicianRepo.save(p);
    }

    // 1. TEST CREATE DEPARTMENT
    @Test
    void testCreateDepartment() throws Exception {
        // Setup: Create a doctor to act as the head
        createTestPhysician(80001);

        // Execute: POST the new department (Notice we use the URI link for the head!)
        String jsonPayload = """
                {
                    "departmentId": 90001,
                    "name": "Neurology",
                    "head": "http://localhost/allPhysician/80001"
                }
                """;

        mockMvc.perform(post("/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated()) 
                .andExpect(header().exists("Location")); 

        // Verify in DB
        Optional<Department> savedDept = departmentRepo.findById(90001);
        assertTrue(savedDept.isPresent());
        assertEquals("Neurology", savedDept.get().getName());
        assertEquals(80001, savedDept.get().getHead().getEmployeeId());
    }

    // 2. TEST UPDATE DEPARTMENT BY ID
    @Test
    void testUpdateDepartmentById() throws Exception {
        // Setup: Save a department directly to the DB first
        Physician head = createTestPhysician(80002);
        departmentRepo.save(new Department(90002, "Old Cardiology", head, null));

        // Execute: PATCH request to update only the name
        String updatePayload = """
                {
                    "name": "Advanced Cardiology"
                }
                """;

        mockMvc.perform(patch("/departments/90002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().is2xxSuccessful()); 

        // Verify it actually changed in the DB
        Department updatedDept = departmentRepo.findById(90002).orElseThrow();
        assertEquals("Advanced Cardiology", updatedDept.getName());
    }

    // 3. TEST UPDATE DEPARTMENT BY NAME
    // (Spring Data REST doesn't allow patching directly to /search/findByName, 
    // so we simulate the frontend fetching by name, getting the ID, and then patching it)
    @Test
    void testUpdateDepartmentByName() throws Exception {
        // Setup: Save a department with a specific name
        Physician head = createTestPhysician(80003);
        departmentRepo.save(new Department(90003, "General Surgery", head, null));

        // Step A: Find the department by its current name (Simulating what the frontend does)
        Page<Department> searchResults = departmentRepo.findByName("General Surgery", PageRequest.of(0, 5));
        assertFalse(searchResults.isEmpty());
        
        // Extract the ID of the department we just found
        int departmentIdToUpdate = searchResults.getContent().get(0).getDepartmentId();

        // Step B: Send the PATCH request to the ID we found
        String updatePayload = """
                {
                    "name": "Specialized Surgery"
                }
                """;

        mockMvc.perform(patch("/departments/" + departmentIdToUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().is2xxSuccessful()); 

        // Verify the name was successfully updated
        Department updatedDept = departmentRepo.findById(departmentIdToUpdate).orElseThrow();
        assertEquals("Specialized Surgery", updatedDept.getName());
    }
}
