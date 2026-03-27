package com.example.HospitalManagement;

import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.PhysicianRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@SpringBootTest
@AutoConfigureMockMvc
class PhysicianRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PhysicianRepository repo;

    // Jackson's ObjectMapper translates our Java objects into JSON strings
    @Autowired
    private ObjectMapper objectMapper;

    // Cleans the database before every single test to prevent "Ghost Data"
    @BeforeEach
    void cleanDatabase() {
        repo.deleteAll();
        repo.flush(); 
    }

    @Test
    void testGetAllPhysicians() throws Exception {
        Physician p1 = new Physician();
        p1.setEmployeeId(1);
        p1.setName("Nikhil_Test"); 
        p1.setPosition("Surgeon");
        p1.setSsn(55555); 

        Physician p2 = new Physician();
        p2.setEmployeeId(2);
        p2.setName("Rahul_Test");
        p2.setPosition("Cardio");
        p2.setSsn(66666);

        repo.save(p1);
        repo.save(p2);

        mockMvc.perform(get("/allPhysician")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.physicians", hasSize(2)));
    }

    @Test
    void testGetAllPhysicians_Pagination() throws Exception {
        for (int i = 0; i < 5; i++) {
            Physician p = new Physician();
            p.setEmployeeId(100 + i); 
            p.setName("Dr. Page " + i);
            p.setPosition("General");
            p.setSsn(77700 + i);      
            repo.save(p);
        }

        mockMvc.perform(get("/allPhysician?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.physicians", hasSize(2)))
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(5))
                .andExpect(jsonPath("$.page.totalPages").value(3)) 
                .andExpect(jsonPath("$.page.number").value(0));    
    }

    @Test
    void testCreatePhysician() throws Exception {
        // 1. Setup: Create the Java object
        Physician newDoc = new Physician();
        newDoc.setEmployeeId(999);
        newDoc.setName("Dr. Strange");
        newDoc.setPosition("Sorcerer Supreme");
        newDoc.setSsn(123999);

        // 2. Convert it to JSON
        String jsonPayload = objectMapper.writeValueAsString(newDoc);

        // 3. Execute: POST the JSON to the API
        mockMvc.perform(post("/allPhysician")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                
                // Expect 201 Created
                .andExpect(status().isCreated()) 
                
                // Option 2 Implementation: Verify the Location header exists 
                // (e.g., http://localhost/allPhysician/999)
                .andExpect(header().exists("Location"))
                
                // Verify the API correctly returned an empty body
                .andExpect(jsonPath("$").doesNotExist());

        // 4. Verify in DB: Since the API didn't echo the data, we ask the DB if it saved!
        List<Physician> foundInDb = repo.findByName("Dr. Strange");
        assertFalse(foundInDb.isEmpty());
        assertEquals("Sorcerer Supreme", foundInDb.get(0).getPosition());
    }


    @Test
    void testUpdatePhysicianName() throws Exception {
        // 1. Setup: Create and save a physician to the database first
        Physician p = new Physician();
        p.setEmployeeId(500);
        p.setName("Dr. John Doe"); // Old name
        p.setPosition("Neurologist");
        p.setSsn(112233);
        
        // Save it and grab the ID that was just used
        Physician savedPhysician = repo.save(p);
        int savedId = savedPhysician.getEmployeeId();

        // 2. Prepare the update payload
        // Since we are using PATCH, we ONLY need to send the field we want to change
        String updatePayload = "{\"name\": \"Dr. John Smith\"}";

        // 3. Execute: Send a PATCH request to /allPhysician/{id}
        mockMvc.perform(patch("/allPhysician/" + savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                
                // Spring Data REST returns 200 OK or 204 No Content on a successful update
                .andExpect(status().is2xxSuccessful()); 

        // 4. Verify in DB: Prove the name changed but the position stayed the same
        Physician updatedPhysician = repo.findById(savedId).orElseThrow();
        
        assertEquals("Dr. John Smith", updatedPhysician.getName()); // Name should be updated
        assertEquals("Neurologist", updatedPhysician.getPosition()); // Position should be untouched
    }


    


}