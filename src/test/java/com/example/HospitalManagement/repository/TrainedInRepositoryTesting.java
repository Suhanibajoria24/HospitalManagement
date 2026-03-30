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

import com.example.HospitalManagement.Entity.TrainedIn;
import com.example.HospitalManagement.Entity.TrainedInId;
import com.example.HospitalManagement.Repository.TrainedInRepository;

@SpringBootTest
public class TrainedInRepositoryTesting {

    @Autowired
    private TrainedInRepository trainedInRepository;

    // Use physician and treatment IDs that already exist in your pre-seeded DB
    private static final Integer TEST_PHYSICIAN = 1003;
    private static final Integer TEST_TREATMENT = 1001;

    @BeforeEach
    void setUp() {
        TrainedIn t = new TrainedIn();
        t.setPhysician(TEST_PHYSICIAN);
        t.setTreatment(TEST_TREATMENT);
        t.setCertificationDate(new Date());
        t.setCertificationExpires(new Date());
        trainedInRepository.save(t);
    }

    @AfterEach
    void tearDown() {
        trainedInRepository.deleteById(new TrainedInId(TEST_PHYSICIAN, TEST_TREATMENT));
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
        Optional<TrainedIn> result = trainedInRepository
                .findByPhysicianAndTreatment(TEST_PHYSICIAN, TEST_TREATMENT);

        assertTrue(result.isPresent());
        assertEquals(TEST_PHYSICIAN, result.get().getPhysician());
        assertEquals(TEST_TREATMENT, result.get().getTreatment());
    }

    @Test
    void testFindByPhysicianAndTreatment_NotFound() {
        Optional<TrainedIn> result = trainedInRepository
                .findByPhysicianAndTreatment(99999, 99999);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByPhysicianAndTreatment_WrongPhysician() {
        Optional<TrainedIn> result = trainedInRepository
                .findByPhysicianAndTreatment(99999, TEST_TREATMENT);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByPhysicianAndTreatment_WrongTreatment() {
        Optional<TrainedIn> result = trainedInRepository
                .findByPhysicianAndTreatment(TEST_PHYSICIAN, 99999);

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
