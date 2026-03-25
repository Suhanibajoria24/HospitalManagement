package com.example.HospitalManagement.apitesting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllPatients_PaginationSuccess() throws Exception{
        mockMvc.perform(get("/patients?page=0&size=5"));
    }

}
