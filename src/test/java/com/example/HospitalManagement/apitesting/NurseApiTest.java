package com.example.HospitalManagement.apitesting;
import com.example.HospitalManagement.Entity.Nurse;
import com.example.HospitalManagement.Repository.NurseRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NurseApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NurseRepository nurseRepository;

    private Nurse nurse1;
    private Nurse nurse2;

    @BeforeEach
    void setUp() {
        nurse1 = new Nurse(301, "Test Nurse A", "Nurse", true, "8881");
        nurse2 = new Nurse(302, "Test Nurse B", "Head Nurse", true, "8882");

        nurseRepository.saveAll(List.of(nurse1, nurse2));
    }
    @AfterEach
    void tearDown() {
        nurseRepository.deleteAll();
    }
    @Test
    void testGetAllNurses_WithProjection() throws Exception {

        mockMvc.perform(get("/nurse?projection=nurseView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.nurses").isArray())
                .andExpect(jsonPath("$._embedded.nurses").isNotEmpty());
    }
    @Test
    void testProjectionFields() throws Exception {

        mockMvc.perform(get("/nurse?projection=nurseView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.nurses[0].name").exists())
                .andExpect(jsonPath("$._embedded.nurses[0].position").exists())
                .andExpect(jsonPath("$._embedded.nurses[0].registered").exists())
                .andExpect(jsonPath("$._embedded.nurses[0].availability").exists())
                .andExpect(jsonPath("$._embedded.nurses[0].ssn").doesNotExist());
    }
    @Test
    void testPagination() throws Exception {

        mockMvc.perform(get("/nurse?page=0&size=2&projection=nurseView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.nurses.length()").value(2));
    }
    @Test
    void testLargeSize() throws Exception {

        mockMvc.perform(get("/nurse?size=1000&projection=nurseView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.nurses").isArray());
    }
}
