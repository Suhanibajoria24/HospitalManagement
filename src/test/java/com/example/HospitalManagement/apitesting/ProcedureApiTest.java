package com.example.HospitalManagement.apitesting;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.dao.DataIntegrityViolationException;
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

@Test
void testAddProcedure_Success() throws Exception {

    String json = """
        {
          "code": 1001,
          "name": "MRI",
          "cost": 3000
        }
        """;

    mockMvc.perform(post("/procedures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
}

@Test
void testAddProcedure_InvalidInput_ShouldThrowException() throws Exception {

    String json = """
        {
          "code": 1003,
          "name": null,
          "cost": null
        }
        """;

    assertThrows(Exception.class, () -> {
        mockMvc.perform(post("/procedures")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn();
    });

}



}
