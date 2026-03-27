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

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    // @AfterEach
    // void deletePhysician(){
    //     physicianRepository.deleteById(100);
    // }

    //pagination test
    @Test
    void testGetAllPatients_PaginationSuccess() throws Exception{
        mockMvc.perform(get("/patients?page=0&size=5"))
        .andExpect(status().isOk())
        .andDo(print());
    }
    

    @Test
    void testGetAllPatients_PageOutOfRange() throws Exception{
        mockMvc.perform(get("/patients?page=10&size=5"))
        .andExpect(status().isOk());
    }

    //get all patients
    @Test
    void testGetAllPatients_DataExists() throws Exception{
        mockMvc.perform(get("/patients"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.patients").exists());
    }

    @Test
    void testGetAllPatients_NoDataExists() throws Exception{
        mockMvc.perform(get("/patients"))
        .andExpect(status().isOk());
    }

    //add patient 
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
    
}
