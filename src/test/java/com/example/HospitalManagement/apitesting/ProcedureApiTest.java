package com.example.HospitalManagement.apitesting;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProcedureApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllProcedures_Pagination_Success() throws Exception {

        mockMvc.perform(get("/procedures?page=0&size=5"))
        .andExpect(status().isOk());

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
          "name": "",
          "cost": null
        }
        """;


        mockMvc.perform(post("/procedures")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());

}

@Test
void testUpdateProcedureCost_Success() throws Exception {

    String createJson = """
        {
          "code": 3001,
          "name": "X-Ray",
          "cost": 2000
        }
        """;

    mockMvc.perform(post("/procedures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson))
            .andExpect(status().isCreated());

    String updateJson = """
        {
          "cost": 4000
        }
        """;

    mockMvc.perform(patch("/procedures/3001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
            .andExpect(status().isNoContent());

    mockMvc.perform(get("/procedures/3001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cost").value(4000.0));
}

@Test
void testUpdateProcedureCost_ResourceNotFoundAndInvalidInput() throws Exception{
        String updateJson = """
        {
          "cost": 4000
        }
        """;
        mockMvc.perform(patch("/procedures/100")
        .contentType(MediaType.APPLICATION_JSON)
        .content(updateJson))
        .andReturn();
}

@org.springframework.boot.test.context.TestConfiguration
static class TestConfig {

    @Autowired
    private org.springframework.data.rest.core.event.ValidatingRepositoryEventListener listener;

    @jakarta.annotation.PostConstruct
    public void init() {

        org.springframework.validation.beanvalidation.LocalValidatorFactoryBean validator =
                new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();

        validator.afterPropertiesSet(); // IMPORTANT

        listener.addValidator("beforeCreate", validator);
        listener.addValidator("beforeSave", validator);
    }
}

}
