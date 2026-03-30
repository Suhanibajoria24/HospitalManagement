package com.example.HospitalManagement;

import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.PhysicianRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest; // <-- Needed for the fix
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional 
public class PhysicianRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PhysicianRepository repo;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllPhysicians() throws Exception {
        Physician p1 = new Physician();
        p1.setEmployeeId(80001); // Safe IDs
        p1.setName("Nikhil_Test"); 
        p1.setPosition("Surgeon");
        p1.setSsn(8885551); 

        Physician p2 = new Physician();
        p2.setEmployeeId(80002);
        p2.setName("Rahul_Test");
        p2.setPosition("Cardio");
        p2.setSsn(8886662);

        repo.save(p1);
        repo.save(p2);

        mockMvc.perform(get("/allPhysician")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllPhysicians_Pagination() throws Exception {
        for (int i = 0; i < 5; i++) {
            Physician p = new Physician();
            p.setEmployeeId(90000 + i); 
            p.setName("Dr. Page " + i);
            p.setPosition("General");
            p.setSsn(99977700 + i);      
            repo.save(p);
        }

        mockMvc.perform(get("/allPhysician?page=1&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.number").value(1));    
    }

    // --- NEW TEST: Verifying your Custom Paginated Search API ---
  @Test
    void testSearchByPosition_Pagination() throws Exception {
        // Use a highly unique position so we don't accidentally count real doctors!
        String uniquePosition = "Astro_Cardiologist";

        for (int i = 0; i < 3; i++) {
            Physician p = new Physician();
            p.setEmployeeId(95000 + i); 
            p.setName("Dr. Heart " + i);
            p.setPosition(uniquePosition); // Assign the fake position
            p.setSsn(11223300 + i);      
            repo.save(p);
        }

        // Search SPECIFICALLY for the fake position
        mockMvc.perform(get("/allPhysician/search/findByPosition?position=" + uniquePosition + "&page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(2))
                // Now this will ALWAYS be exactly 3, no matter what is in your real DB!
                .andExpect(jsonPath("$.page.totalElements").value(3)) 
                .andExpect(jsonPath("$.page.totalPages").value(2));   
    }

    @Test
    void testCreatePhysician() throws Exception {
        Physician newDoc = new Physician();
        newDoc.setEmployeeId(97900);
        newDoc.setName("Dr. Strange");
        newDoc.setPosition("Sorcerer Supreme");
        newDoc.setSsn(12399900);

        String jsonPayload = objectMapper.writeValueAsString(newDoc);

        mockMvc.perform(post("/allPhysician")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated()) 
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$").doesNotExist());

        // FIX: Update this line to use pagination since the Repository method changed!
        Page<Physician> foundInDb = repo.findByName("Dr. Strange", PageRequest.of(0, 5));
        
        assertFalse(foundInDb.isEmpty());
        // Use .getContent() to read the data inside the Page
        assertEquals("Sorcerer Supreme", foundInDb.getContent().get(0).getPosition());
    }

    @Test
    void testUpdatePhysicianName() throws Exception {
        Physician p = new Physician();
        p.setEmployeeId(50000);
        p.setName("Dr. John Doe"); 
        p.setPosition("Neurologist");
        p.setSsn(11223388);
        
        Physician savedPhysician = repo.save(p);
        int savedId = savedPhysician.getEmployeeId();

        String updatePayload = "{\"name\": \"Dr. John Smith\"}";

        mockMvc.perform(patch("/allPhysician/" + savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().is2xxSuccessful()); 

        Physician updatedPhysician = repo.findById(savedId).orElseThrow();
        
        assertEquals("Dr. John Smith", updatedPhysician.getName()); 
        assertEquals("Neurologist", updatedPhysician.getPosition()); 
    }
}