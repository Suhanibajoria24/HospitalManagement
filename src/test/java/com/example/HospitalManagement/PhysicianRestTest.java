package com.example.HospitalManagement;

import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.PhysicianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
class PhysicianRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PhysicianRepository repo;

    // THIS IS THE CURE: It ruthlessly deletes all ghost data before EVERY test starts
    @BeforeEach
    void cleanDatabase() {
        repo.deleteAll();
        repo.flush(); // Forces the database to immediately apply the deletion
    }

    @Test
    void testGetAllPhysicians() throws Exception {
        // Insert brand new test data
        Physician p1 = new Physician();
        p1.setEmployeeId(1);
        p1.setName("Nikhil_Test"); 
        p1.setPosition("Surgeon");
        p1.setSsn(55555); // Changed to avoid any random lingering constraints

        Physician p2 = new Physician();
        p2.setEmployeeId(2);
        p2.setName("Rahul_Test");
        p2.setPosition("Cardio");
        p2.setSsn(66666);

        repo.save(p1);
        repo.save(p2);

        // Call REST API - It will now reliably find exactly 2 items
        mockMvc.perform(get("/allPhysician")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.physicians", hasSize(2)));
    }

}