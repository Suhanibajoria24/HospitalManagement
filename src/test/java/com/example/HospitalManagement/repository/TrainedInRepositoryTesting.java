package com.example.HospitalManagement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Entity.Procedure;
import com.example.HospitalManagement.Entity.TrainedIn;
import com.example.HospitalManagement.Entity.TrainedInId;
import com.example.HospitalManagement.Repository.PhysicianRepository;
import com.example.HospitalManagement.Repository.ProcedureRepository;
import com.example.HospitalManagement.Repository.TrainedInRepository;

@SpringBootTest
public class TrainedInRepositoryTesting {

    @Autowired
    private TrainedInRepository trainedInRepository;

    @Autowired
private PhysicianRepository physicianRepository;

@Autowired
private ProcedureRepository procedureRepository;

private static final Integer TEST_PHYSICIAN = 1003;
private static final Integer TEST_TREATMENT = 1001;

@BeforeEach
void setUp() {
    // Create physician if not exists
    if (!physicianRepository.existsById(TEST_PHYSICIAN)) {
        Physician physician = new Physician();
        physician.setEmployeeId(TEST_PHYSICIAN);
        physician.setName("Dr Test");
        physician.setPosition("Physician");
        physician.setSsn(123456789);
        physicianRepository.save(physician);
    }

    // Create procedure if not exists
    if (!procedureRepository.existsById(TEST_TREATMENT)) {
        Procedure procedure = new Procedure();
        procedure.setCode(TEST_TREATMENT);
        procedure.setName("Test Procedure");
        procedure.setCost(100.0);  // was missing — @NotNull in Procedure entity
        procedureRepository.save(procedure);
    }

    // Create trained_in record
    if (!trainedInRepository.existsById(new TrainedInId(TEST_PHYSICIAN, TEST_TREATMENT))) {
        TrainedIn t = new TrainedIn();
        t.setPhysician(TEST_PHYSICIAN);
        t.setTreatment(TEST_TREATMENT);
        t.setCertificationDate(new Date());
        t.setCertificationExpires(new Date());
        trainedInRepository.save(t);
    }
}
@AfterEach
void tearDown() {
    // Delete in FK order — child first, then parents
    if (trainedInRepository.existsById(new TrainedInId(TEST_PHYSICIAN, TEST_TREATMENT))) {
        trainedInRepository.deleteById(new TrainedInId(TEST_PHYSICIAN, TEST_TREATMENT));
    }

    if (physicianRepository.existsById(TEST_PHYSICIAN)) {
        physicianRepository.deleteById(TEST_PHYSICIAN);
    }

    if (procedureRepository.existsById(TEST_TREATMENT)) {
        procedureRepository.deleteById(TEST_TREATMENT);
    }
}

    // --- findByTreatment ---

    @Test
    void testFindByTreatment_Success() {
        Page<TrainedIn> result = trainedInRepository.findByTreatment(
                TEST_TREATMENT, PageRequest.of(0, 5));

        assertFalse(result.isEmpty());
        assertTrue(result.getContent().stream()
                .anyMatch(t -> t.getTreatment().equals(TEST_TREATMENT)));
    }

    @Test
    void testFindByTreatment_NotFound() {
        Page<TrainedIn> result = trainedInRepository.findByTreatment(
                99999, PageRequest.of(0, 5));

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByTreatment_Pagination() {
        Page<TrainedIn> result = trainedInRepository.findByTreatment(
                TEST_TREATMENT, PageRequest.of(0, 5));

        assertNotNull(result);
        assertEquals(0, result.getNumber());      // page number
        assertEquals(5, result.getSize());         // page size
    }

    // --- findByPhysicianAndTreatment ---

@Test
void testFindByPhysicianAndTreatment_Success() {
    Page<TrainedIn> result = trainedInRepository
            .findByPhysicianAndTreatment(TEST_PHYSICIAN, TEST_TREATMENT, PageRequest.of(0, 5));

    assertFalse(result.isEmpty());
    assertEquals(TEST_PHYSICIAN, result.getContent().get(0).getPhysician());
    assertEquals(TEST_TREATMENT, result.getContent().get(0).getTreatment());
}

@Test
void testFindByPhysicianAndTreatment_NotFound() {
    Page<TrainedIn> result = trainedInRepository
            .findByPhysicianAndTreatment(99999, 99999, PageRequest.of(0, 5));

    assertTrue(result.isEmpty());
}

@Test
void testFindByPhysicianAndTreatment_WrongPhysician() {
    Page<TrainedIn> result = trainedInRepository
            .findByPhysicianAndTreatment(99999, TEST_TREATMENT, PageRequest.of(0, 5));

    assertTrue(result.isEmpty());
}

@Test
void testFindByPhysicianAndTreatment_WrongTreatment() {
    Page<TrainedIn> result = trainedInRepository
            .findByPhysicianAndTreatment(TEST_PHYSICIAN, 99999, PageRequest.of(0, 5));

    assertTrue(result.isEmpty());
}

    // --- save ---

    @Test
    void testSaveTrainedIn_Success() {
        TrainedIn t = new TrainedIn();
        t.setPhysician(TEST_PHYSICIAN); 
        t.setTreatment(TEST_TREATMENT);
        t.setCertificationDate(new Date());
        t.setCertificationExpires(new Date());

        TrainedIn saved = trainedInRepository.save(t);

        assertNotNull(saved);
        assertEquals(1003, saved.getPhysician());
        assertEquals(TEST_TREATMENT, saved.getTreatment());

        // cleanup
        trainedInRepository.deleteById(new TrainedInId(1003, TEST_TREATMENT));
    }
}
