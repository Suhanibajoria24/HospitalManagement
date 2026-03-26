package com.example.HospitalManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MedicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetMedication() throws Exception {

        // use existing data from DB (code = 1)
        mockMvc.perform(get("/medications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Procrastin-X")); 
    }

    @Test
    void testMedicationNotFound() throws Exception {
        mockMvc.perform(get("/medications/9999"))
                .andExpect(status().isNotFound());
    }
}