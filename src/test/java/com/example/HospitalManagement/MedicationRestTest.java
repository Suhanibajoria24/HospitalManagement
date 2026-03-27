package com.example.HospitalManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;

import com.example.HospitalManagement.Entity.Medication;
import com.example.HospitalManagement.Repository.MedicationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class MedicationRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MedicationRepository repo;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        repo.deleteAll();
        repo.flush();
    }

    // Test case 1 : Test whether we are getting All Medications
    @Test
    void testGetAllMedications() throws Exception {

        Medication m1 = new Medication();
        m1.setCode(10);
        m1.setName("Med1");
        m1.setBrand("Brand1");
        m1.setDescription("Desc1");

        Medication m2 = new Medication();
        m2.setCode(20);
        m2.setName("Med2");
        m2.setBrand("Brand2");
        m2.setDescription("Desc2");

        repo.save(m1);
        repo.save(m2);

        mockMvc.perform(get("/medications")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.medications", hasSize(2)));
    }
    // GET
    // http://localhost:9090/allMedications



    // Test Case 2 : get medication with a particular id
    @Test
    void testGetMedicationById() throws Exception {

        Medication m = new Medication();
        m.setCode(30);
        m.setName("Paracetamol");
        m.setBrand("ABC");
        m.setDescription("Painkiller");

        repo.save(m);

        mockMvc.perform(get("/medications/30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Paracetamol"));
    }
    // GET
    // http://localhost:9090/allMedications/30


    // Test 3 : Create Medication Test
    @Test
    void testCreateMedication() throws Exception {

        Medication med = new Medication();
        med.setCode(101);
        med.setName("NewMed101");
        med.setBrand("BrandX101");
        med.setDescription("Test101");

        String json = objectMapper.writeValueAsString(med);

        mockMvc.perform(post("/allMedications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        List<Medication> list = repo.findByName("NewMed101");

        assertFalse(list.isEmpty());
    }
    // POST
    // http://localhost:9090/allMedications




    // Test 4 : Update Medication Test
    @Test
    void testUpdateMedication() throws Exception {

        Medication m = new Medication();
        m.setCode(50);
        m.setName("OldName");
        m.setBrand("Brand");
        m.setDescription("Desc");

        repo.save(m);

        String payload = "{\"name\": \"UpdatedName\"}";

        mockMvc.perform(patch("/allMedications/50")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().is2xxSuccessful());

        Medication updated = repo.findById(50).orElseThrow();

        assertEquals("UpdatedName", updated.getName());
    }
    // PATCH
    // http://localhost:9090/allMedications/50

    

    @Test
    void testMedicationNotFound() throws Exception {
        mockMvc.perform(get("/medications/9999"))
                .andExpect(status().isNotFound());
    }
}