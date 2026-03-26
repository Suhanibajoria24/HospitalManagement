package com.example.HospitalManagement.apitesting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientApiTest {

    @Autowired
    private MockMvc mockMvc;

    //pagination test
    @Test
    void testGetAllPatients_PaginationSuccess() throws Exception{
        mockMvc.perform(get("/patients?page=0&size=5"));
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
    
}
