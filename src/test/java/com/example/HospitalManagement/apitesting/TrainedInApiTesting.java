package com.example.HospitalManagement.apitesting;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.HospitalManagement.Entity.TrainedInId;
import com.example.HospitalManagement.Repository.TrainedInRepository;

@SpringBootTest
@AutoConfigureMockMvc

public class TrainedInApiTesting {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainedInRepository trainedInRepository;

@AfterEach
void tearDown() {
    if (trainedInRepository.existsById(new TrainedInId(1001, 1003))) {
        trainedInRepository.deleteById(new TrainedInId(1001, 1003));
    }
}

@Test
void testFindTrainedIn_ByTreatment() throws Exception {

    mockMvc.perform(get("/trainedIn/search/findByTreatment")
            .param("treatment", "1001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.trainedIns").exists());
}
@Test
void testGetCertificationsByProcedure_Pagination_Success() throws Exception {

    mockMvc.perform(get("/trainedIn/search/findByTreatment")
            .param("treatment", "1001")
            .param("page", "0")
            .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.trainedIns").exists())
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
// @Test
// void testAddCertification_DuplicateEntry_ShouldThrowException() throws Exception {

//     String json = """
//         {
//           "physician": 1003,
//           "treatment": 1001,
//           "certificationDate": "2024-01-01T00:00:00.000+00:00",
//           "certificationExpires": "2026-01-01T00:00:00.000+00:00"
//         }
//         """;

//         mockMvc.perform(post("/trainedIn")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(json))
//                 .andExpect(status().isBadRequest()); 
//     }
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




