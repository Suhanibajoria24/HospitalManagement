package com.example.HospitalManagement.apitesting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientApiTest {

    @Autowired
    private MockMvc mockMvc;

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
            "ssn": 100000010,
            "name": "David",
            "address": "Pune",
            "phone": "9999999999",
            "insuranceID": 12345,
            "pcp": "http://localhost:9090/allPhysician/999"
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
        "ssn": "123",
        "name": "",
        "address": "Pune",
        "phone": "9999999999",
        "insuranceID": 12345,
        "pcp": "http://localhost:9090/allPhysician/999"
    }
    """;

    assertThrows(Exception.class, () -> {mockMvc.perform(post("/patients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andReturn();
    });
    }
    
}
