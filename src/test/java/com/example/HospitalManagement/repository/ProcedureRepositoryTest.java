package com.example.HospitalManagement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.example.HospitalManagement.Entity.Procedure;
import com.example.HospitalManagement.Repository.ProcedureRepository;
@SpringBootTest
public class ProcedureRepositoryTest {
    @Autowired
    private ProcedureRepository procedureRepository;

    private List<Procedure> procedures;

    @BeforeEach
    public void setUp() {

        Procedure p1 = new Procedure();
        p1.setCode(200);
        p1.setName("Heart Surgery");
        p1.setCost(15000.0);

        Procedure p2 = new Procedure();
        p2.setCode(201);
        p2.setName("Brain Surgery");
        p2.setCost(20000.0);

        Procedure p3 = new Procedure();
        p3.setCode(202);
        p3.setName("Heart Surgery"); 
        p3.setCost(18000.0);

        procedures = Arrays.asList(p1, p2, p3);
        procedureRepository.saveAll(procedures);
    }

    @AfterEach
    public void flushData() {
        procedureRepository.deleteAll(procedures);
    }

    @Test
    @Transactional
    @Rollback
    void testSaveProcedure() {

        Procedure procedure = new Procedure();
        procedure.setCode(1001);
        procedure.setName("Kidney Surgery");
        procedure.setCost(12000.0);

        Procedure saved = procedureRepository.save(procedure);

        assertNotNull(saved.getCode());
        assertEquals("Kidney Surgery", saved.getName());
        assertEquals(12000.0, saved.getCost());
}


}
