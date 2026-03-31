package com.example.HospitalManagement.apitesting;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

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

import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Entity.Procedure;
import com.example.HospitalManagement.Entity.TrainedIn;
import com.example.HospitalManagement.Entity.TrainedInId;
import com.example.HospitalManagement.Repository.PhysicianRepository;
import com.example.HospitalManagement.Repository.ProcedureRepository;
import com.example.HospitalManagement.Repository.TrainedInRepository;

@SpringBootTest
@AutoConfigureMockMvc

public class TrainedInApiTesting {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainedInRepository trainedInRepository;


    @Autowired
    private PhysicianRepository physicianRepository;

    @Autowired
    private ProcedureRepository procedureRepository;

    private static final Integer TEST_PHYSICIAN = 1003;
    private static final Integer TEST_TREATMENT = 1001;

    private boolean physicianCreatedByTest = false;
    private boolean procedureCreatedByTest = false;
    private boolean trainedInCreatedByTest = false;

@BeforeEach
void setUp(TestInfo testInfo) {
    if (!physicianRepository.existsById(TEST_PHYSICIAN)) {
        Physician physician = new Physician();
        physician.setEmployeeId(TEST_PHYSICIAN);
        physician.setName("Dr test");
        physician.setPosition("Physician");
        physician.setSsn(123456789);
        physicianRepository.save(physician);
        physicianCreatedByTest = true;
    }

    if (!procedureRepository.existsById(TEST_TREATMENT)) {
        Procedure procedure = new Procedure();
        procedure.setCode(TEST_TREATMENT);
        procedure.setName("Test Procedure");
        procedure.setCost(100.0);
        procedureRepository.save(procedure);
        procedureCreatedByTest = true;
    }

    // Tests that create their own trained_in record — skip pre-creation
    String testName = testInfo.getTestMethod().get().getName();
    boolean testCreatesOwnRecord = testName.equals("testAddTrainedIn_Success")
            || testName.equals("testRenewCertification_Success");


    if (!testCreatesOwnRecord &&
            !trainedInRepository.existsById(new TrainedInId(TEST_PHYSICIAN, TEST_TREATMENT))) {
        TrainedIn t = new TrainedIn();
        t.setPhysician(TEST_PHYSICIAN);
        t.setTreatment(TEST_TREATMENT);
        t.setCertificationDate(new Date());
        t.setCertificationExpires(new Date(System.currentTimeMillis() + 86400000L * 365));
        trainedInRepository.save(t);
        trainedInCreatedByTest = true;
    }
}

@AfterEach
void tearDown() {
    if (trainedInRepository.existsById(new TrainedInId(TEST_PHYSICIAN, TEST_TREATMENT))) {
        trainedInRepository.deleteById(new TrainedInId(TEST_PHYSICIAN, TEST_TREATMENT));
    }
    trainedInCreatedByTest = false;

    if (physicianCreatedByTest && physicianRepository.existsById(TEST_PHYSICIAN)) {
        physicianRepository.deleteById(TEST_PHYSICIAN);
        physicianCreatedByTest = false;
    }

    if (procedureCreatedByTest && procedureRepository.existsById(TEST_TREATMENT)) {
        procedureRepository.deleteById(TEST_TREATMENT);
        procedureCreatedByTest = false;
    }
}

@Test
void testFindTrainedIn_ByTreatment() throws Exception {
    mockMvc.perform(get("/trainedIn/search/findByTreatment")
            .param("treatment", "1001")
            .param("projection", "viewCertified")
            .accept("application/hal+json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.trainedIns").exists())
            .andExpect(jsonPath("$._embedded.trainedIns[0].certificationDate").exists())
            .andExpect(jsonPath("$._embedded.trainedIns[0].certificationExpires").exists())
            .andExpect(jsonPath("$._embedded.trainedIns[0].hasExpired").value(false))
            .andExpect(jsonPath("$._embedded.trainedIns[0].physicianEntity.name").value("Dr test"));
}

@Test
void testGetCertificationsByProcedure_Pagination_Success() throws Exception {
    mockMvc.perform(get("/trainedIn/search/findByTreatment")
            .param("treatment", "1001")
            .param("projection", "viewCertified")
            .param("page", "0")
            .param("size", "5")
            .accept("application/hal+json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.trainedIns").exists())
            .andExpect(jsonPath("$._embedded.trainedIns[0].physicianEntity.name").value("Dr test"))
            .andExpect(jsonPath("$._embedded.trainedIns[0].hasExpired").value(false))
            .andExpect(jsonPath("$.page").exists())
            .andExpect(jsonPath("$.page.size").value(5));
}


@Test
void testAddTrainedIn_Success() throws Exception {

    String json = """
        {
          "physician": 1003,
          "treatment": 1001,
          "certificationDate": "2024-01-01",
          "certificationExpires": "2026-01-01"
        }
        """;

    mockMvc.perform(post("/trainedIn")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
    
}
@Test
void testAddCertification_InvalidPhysician() throws Exception{

    String json = """
        {
          "physician": 99999,
          "treatment": 1001,
          "certificationDate": "2024-01-01",
          "certificationExpires": "2026-01-01"
        }
        """;

        mockMvc.perform(post("/trainedIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
                
    }
@Test
void testAddCertification_DuplicateEntry_ShouldReturn409() throws Exception {

    String json = """
        {
          "physician": 1003,
          "treatment": 1001,
          "certificationDate": "2024-01-01T00:00:00.000+00:00",
          "certificationExpires": "2026-01-01T00:00:00.000+00:00"
        }
        """;
    // Second POST — should be duplicate
    mockMvc.perform(post("/trainedIn")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isConflict());
}
@Test
void testRenewCertification_Success() throws Exception {

    // Step 1: Create record first
    mockMvc.perform(post("/trainedIn")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "physician": 1003,
                  "treatment": 1001,
                  "certificationDate": "2024-01-01T00:00:00.000+00:00",
                  "certificationExpires": "2026-01-01T00:00:00.000+00:00"
                }
                """))
            .andExpect(status().isCreated());

    // Step 2: Renew certification
    mockMvc.perform(patch("/trainedIn/renew/1001/1003")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "certificationExpires": "2028-01-01T00:00:00.000+00:00"
                }
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.physician").value(1003))
            .andExpect(jsonPath("$.treatment").value(1001))
            .andExpect(jsonPath("$.certificationExpires").value("2028-01-01T00:00:00.000+00:00"));
}

@Test
void testRenewCertification_NotFound_ShouldReturn404() throws Exception {

    mockMvc.perform(patch("/trainedIn/renew/9999/9999")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "certificationExpires": "2028-01-01T00:00:00.000+00:00"
                }
                """))
            .andExpect(status().isNotFound());
}

}




