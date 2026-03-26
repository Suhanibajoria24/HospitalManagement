package com.example.HospitalManagement;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.HospitalManagement.Entity.Medication;
import com.example.HospitalManagement.Repository.MedicationRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class MedicationRepositoryTest {
    @Autowired
    private MedicationRepository repository;

    @Test
    void testSaveAndFind() {
        Medication med = new Medication();
        med.setCode(1);
        med.setName("Paracetamol");

        repository.save(med);

        Optional<Medication> found = repository.findById(1);

        assertTrue(found.isPresent());
    }
}
