package com.example.HospitalManagement.apitesting;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.PhysicianRepository;

import jakarta.transaction.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PatientApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PhysicianRepository physicianRepository;

    @BeforeEach
    void seedPcpPhysician() {
        Physician pcp = new Physician();
        pcp.setEmployeeId(100);
        pcp.setName("Dr. PCP");
        pcp.setPosition("General");
        pcp.setSsn(888888888);
        physicianRepository.save(pcp);
    }

    // pagination test
    @Test
    void testGetAllPatients_PaginationSuccess() throws Exception {
        mockMvc.perform(get("/patients?page=0&size=5"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testGetAllPatients_PageOutOfRange() throws Exception {
        mockMvc.perform(get("/patients?page=10&size=5"))
                .andExpect(status().isOk());
    }


    @Test
    void testGetAllPatients_NoDataExists() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk());
    }

    // add patient
    @Test
    void testCreatePatient_ValidData_ReturnsCreated() throws Exception {

        String patientJson = """
                {
                    "ssn": 100000018,
                    "name": "ved",
                    "address": "NGP",
                    "phone": "9999999999",
                    "insuranceID": 12345,
                    "pcp": "/allPhysician/100"
                }
                """;

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientJson))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    void testCreatePatient_InvalidFormat_ReturnsBadRequest() throws Exception {

        String invalidJson = """
                {
                    "ssn": "AB123",
                    "name": "",
                    "address": "Pune",
                    "phone": "9999999999",
                    "insuranceID": 12345,
                    "pcp": "http://localhost:9090/allPhysician/100"
                }
                """;

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // update the patient
    @Test
    void testUpdatePatientPhone_Success() throws Exception {
        // 1. Create a Patient first (Linking to Physician 100 from your @BeforeEach)
        String createJson = """
                {
                    "ssn": 100000020,
                    "name": "Arjun",
                    "address": "Nagpur",
                    "phone": "1234567890",
                    "insuranceID": 55555,
                    "pcp": "/physicians/100"
                }
                """;

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
                .andExpect(status().isCreated());

        // 2. Update only the phone number
        String updateJson = """
                {
                    "phone": "9876543210"
                }
                """;

        mockMvc.perform(patch("/patients/100000020")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isNoContent()); // Spring Data REST returns 204 No Content for PATCH/PUT

        // 3. Verify the change
        mockMvc.perform(get("/patients/100000020"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("9876543210"));
    }

    @Test
    void testUpdatePatient_NotFound() throws Exception {
        String updateJson = "{\"address\": \"Mumbai\"}";

        // Attempting to update a non-existent SSN
        mockMvc.perform(patch("/patients/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isNotFound());
    }

}
