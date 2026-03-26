package com.example.HospitalManagement;

import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.PhysicianRepository;
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

    @Test
    void testGetAllPhysicians() throws Exception {

        // Clean DB
        repo.deleteAll();

        // Insert test data
        Physician p1 = new Physician();
        p1.setName("Nikhil");
        p1.setPosition("Surgeon");
        p1.setSsn(11111);

        Physician p2 = new Physician();
        p2.setName("Rahul");
        p2.setPosition("Cardio");
        p2.setSsn(22222);

        repo.save(p1);
        repo.save(p2);

        // Call REST API
        mockMvc.perform(get("/allPhysician")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.physicians", hasSize(2)));
    }

    @Test
    void testGetAllPhysicians_EmptyDB() throws Exception {


        repo.deleteAll();


        mockMvc.perform(get("/allPhysician")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.physicians").isArray())
                .andExpect(jsonPath("$._embedded.physicians").isEmpty());
    }
    @Test
    void testGetAllPhysicians_() throws Exception {


        repo.deleteAll();


        mockMvc.perform(get("/allPhysician")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.physicians").isArray())
                .andExpect(jsonPath("$._embedded.physicians").isEmpty());
    }



}