package com.example.HospitalManagement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import com.example.HospitalManagement.Entity.Medication;
import com.example.HospitalManagement.Repository.MedicationRepository;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class MedicationRepositoryTest {
    @Autowired
    private MedicationRepository repository;

    @BeforeEach
    void setUp(){
        repository.deleteAll();
    }

    @Test
    void testFindById_Exists() {

        Medication medication = new Medication();
        medication.setCode(1);
        medication.setName("Procrastin-X");
        medication.setBrand("TestBrand");
        medication.setDescription("Test medication");
        repository.save(medication);

        Optional<Medication> med = repository.findById(1);

        assertTrue(med.isPresent());
        assertEquals("Procrastin-X", med.get().getName());
    }

    @Test
    void testFindById_NotExists() {

        Optional<Medication> med = repository.findById(9999);

        assertFalse(med.isPresent());
    }
}
