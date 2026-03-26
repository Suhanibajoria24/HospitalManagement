package com.example.HospitalManagement.apitesting;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.HospitalManagement.Entity.Procedure;
import com.example.HospitalManagement.Repository.ProcedureRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ProcedureApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProcedureRepository procedureRepository;

    @Test
    void testGetAllProcedures_Pagination_Success() throws Exception {

        mockMvc.perform(get("/procedures?page=0&size=5"));

}
    @Test
    void testGetAllProcedures_NoData_ShouldReturnEmpty() throws Exception {

        mockMvc.perform(get("/procedures?page=0&size=5"))
            .andDo(print())
            .andExpect(status().isOk());
}



}
