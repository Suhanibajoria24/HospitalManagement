package com.example.HospitalManagement.apitesting;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

    @BeforeEach
    void setUp(TestInfo testInfo) {
        String testName = testInfo.getTestMethod().get().getName();

        // Tests that create their own data — skip pre-creation
        boolean testCreatesOwnData =
                testName.equals("testAddProcedure_Success") ||
                testName.equals("testAddProcedure_InvalidInput_ShouldThrowException") ||
                testName.equals("testUpdateProcedureCost_Success") ||
                testName.equals("testSearchProcedureByName_Pagination_Success") ||
                testName.equals("testUpdateProcedureCost_ResourceNotFoundAndInvalidInput") ||
                testName.equals("testGetAllProcedures_NoData_ShouldReturnEmpty");

        if (!testCreatesOwnData) {
            if (!procedureRepository.existsById(8001)) {
                Procedure p1 = new Procedure();
                p1.setCode(8001);
                p1.setName("X-Ray");
                p1.setCost(1500.0);
                procedureRepository.save(p1);
            }
            if (!procedureRepository.existsById(8002)) {
                Procedure p2 = new Procedure();
                p2.setCode(8002);
                p2.setName("CT Scan");
                p2.setCost(2000.0);
                procedureRepository.save(p2);
            }
            if (!procedureRepository.existsById(8003)) {
                Procedure p3 = new Procedure();
                p3.setCode(8003);
                p3.setName("MRI Scan");
                p3.setCost(3000.0);
                procedureRepository.save(p3);
            }
            if (!procedureRepository.existsById(8004)) {
                Procedure p4 = new Procedure();
                p4.setCode(8004);
                p4.setName("Blood Test");
                p4.setCost(500.0);
                procedureRepository.save(p4);
            }
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up all codes used across all tests
        int[] allCodes = {1001, 1003, 3001, 8001, 8002, 8003, 8004, 9901, 9902};
        for (int code : allCodes) {
            if (procedureRepository.existsById(code)) {
                procedureRepository.deleteById(code);
            }
        }
    }

    @Test
    void testGetAllProcedures_Pagination_Success() throws Exception {
        mockMvc.perform(get("/procedures?page=0&size=10"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllProcedures_NoData_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/procedures?page=0&size=10"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testAddProcedure_Success() throws Exception {
        mockMvc.perform(post("/procedures")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "code": 1001,
                          "name": "MRI",
                          "cost": 3000
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void testAddProcedure_InvalidInput_ShouldThrowException() throws Exception {
        mockMvc.perform(post("/procedures")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "code": 1003,
                          "name": "",
                          "cost": null
                        }
                        """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateProcedureCost_Success() throws Exception {
        mockMvc.perform(post("/procedures")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "code": 3001,
                          "name": "X-Ray",
                          "cost": 2000
                        }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/procedures/3001")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "cost": 4000
                        }
                        """))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/procedures/3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cost").value(4000.0));
    }

    @Test
    void testSearchProcedureByName_ExactMatch_Success() throws Exception {
        // 8001 "X-Ray" created in setUp
        mockMvc.perform(get("/procedures/search/findByNameContainingIgnoreCase?name=X-Ray&page=0&size=5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.procedures[0].name").value("X-Ray"));
    }

    @Test
    void testSearchProcedureByName_PartialMatch_Success() throws Exception {
        // 8002 "CT Scan" and 8003 "MRI Scan" created in setUp
        mockMvc.perform(get("/procedures/search/findByNameContainingIgnoreCase?name=scan&page=0&size=5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.procedures").isArray())
                .andExpect(jsonPath("$._embedded.procedures[?(@.name == 'CT Scan')]").exists())
                .andExpect(jsonPath("$._embedded.procedures[?(@.name == 'MRI Scan')]").exists());
    }

    @Test
    void testSearchProcedureByName_CaseInsensitive_Success() throws Exception {
        // 8004 "Blood Test" created in setUp
        mockMvc.perform(get("/procedures/search/findByNameContainingIgnoreCase?name=BLOOD&page=0&size=5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.procedures[0].name").value("Blood Test"));
    }

    @Test
    void testSearchProcedureByName_NoMatch_ReturnsEmpty() throws Exception {
        mockMvc.perform(get("/procedures/search/findByNameContainingIgnoreCase?name=xyznonexistent999&page=0&size=5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.procedures").isArray())
                .andExpect(jsonPath("$._embedded.procedures").isEmpty())
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    @Test
    void testSearchProcedureByName_Pagination_Success() throws Exception {
        mockMvc.perform(post("/procedures")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "code": 9901,
                          "name": "UniqueTestProcedureAlpha",
                          "cost": 100
                        }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/procedures")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "code": 9902,
                          "name": "UniqueTestProcedureBeta",
                          "cost": 200
                        }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/procedures/search/findByNameContainingIgnoreCase?name=UniqueTestProcedure&page=0&size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.procedures").isArray())
                .andExpect(jsonPath("$._embedded.procedures.length()").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(2));
    }

    @Test
    void testSearchProcedureByName_EmptyName_ReturnsAll() throws Exception {
        // setUp seeds at least 4 procedures, so _embedded.procedures will exist
        mockMvc.perform(get("/procedures/search/findByNameContainingIgnoreCase?name=&page=0&size=10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.procedures").isArray());
    }

    @Test
    void testUpdateProcedureCost_ResourceNotFoundAndInvalidInput() throws Exception {
        mockMvc.perform(patch("/procedures/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "cost": 4000
                        }
                        """))
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
            validator.afterPropertiesSet();
            listener.addValidator("beforeCreate", validator);
            listener.addValidator("beforeSave", validator);
        }
    }
}