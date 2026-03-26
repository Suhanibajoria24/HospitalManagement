package com.example.HospitalManagement.repository;

import com.example.HospitalManagement.Entity.Nurse;
import com.example.HospitalManagement.Repository.NurseRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NurseRepositoryTest {

    @Autowired
    private NurseRepository nurseRepository;

    private Nurse nurse1;
    private Nurse nurse2;
    @BeforeEach
    void setUp() {
        nurse1 = new Nurse(201, "Test Nurse 1", "Nurse", true, "9991");
        nurse2 = new Nurse(202, "Test Nurse 2", "Nurse", true, "9992");

        nurseRepository.saveAll(List.of(nurse1, nurse2));
    }
    @AfterEach
    void tearDown() {
        nurseRepository.deleteAll();
    }
    @Test
    void testFindAll_DataExists() {

        Page<Nurse> result = nurseRepository.findAll(PageRequest.of(0, 5));

        assertNotNull(result);
        assertTrue(result.getContent().size() >= 2);
    }
    @Test
    void testFindAll_Pagination() {

        Page<Nurse> result = nurseRepository.findAll(PageRequest.of(0, 2));

        assertEquals(2, result.getContent().size());
    }
    @Test
    void testFindAll_InvalidPage() {

        assertThrows(IllegalArgumentException.class, () -> {
            nurseRepository.findAll(PageRequest.of(-1, 5));
        });
    }
}
