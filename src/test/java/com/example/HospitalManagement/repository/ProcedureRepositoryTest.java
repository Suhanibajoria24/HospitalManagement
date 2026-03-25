package com.example.HospitalManagement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.HospitalManagement.Entity.Procedure;
import com.example.HospitalManagement.Repository.ProcedureRepository;
@SpringBootTest
public class ProcedureRepositoryTest {
    @Autowired
    private ProcedureRepository procedureRepository;

    @Test
    void testSaveAndFind() {
        Procedure p = new Procedure(1001, "TEST_PROC", 2000.0);
        procedureRepository.save(p);
        Procedure found = procedureRepository.findById(1001).orElse(null);

        assertNotNull(found);
        assertEquals("TEST_PROC", found.getName());
        procedureRepository.deleteById(1001);
    }

}
