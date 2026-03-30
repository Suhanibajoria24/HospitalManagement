package com.example.HospitalManagement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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
void setUp() {
    Procedure p = new Procedure();
    p.setCode(55555);
    p.setName("Heart Surgery");
    p.setCost(12000.0);
    procedureRepository.save(p);
}

@AfterEach
void tearDown() {
    procedureRepository.deleteById(55555);
}


    @Test

    void testSaveProcedure() {

        Procedure procedure = new Procedure();
        procedure.setCode(77777);
        procedure.setName("Kidney Surgery");
        procedure.setCost(12000.0);

        Procedure saved = procedureRepository.save(procedure);

        assertNotNull(saved.getCode());
        assertEquals("Kidney Surgery", saved.getName());
        assertEquals(12000.0, saved.getCost());
        procedureRepository.deleteById(77777);
}

@Test
void testFindByName_Success() {

    List<Procedure> found = procedureRepository.findByNameIgnoreCase("Heart Surgery");
    assertFalse(found.isEmpty());

}

@Test
void testFindByName_NotFound() {
    List<Procedure> found = procedureRepository.findByNameIgnoreCase("Eye Surgery");
    assertTrue(found.isEmpty());
}

@Test
void testFindByName_EmptyString() {
    List<Procedure> found = procedureRepository.findByNameIgnoreCase("");
    assertTrue(found.isEmpty());
}

@Test
void testFindByName_InvalidInput() {
    List<Procedure> found = procedureRepository.findByNameIgnoreCase("12345");
    assertTrue(found.isEmpty());
}

@Test
void testFindByCode_Success() {
    Procedure found = procedureRepository.findById(55555).orElse(null);
    assertNotNull(found);
    assertEquals("Heart Surgery", found.getName());
}

@Test
void testFindByCode_NotFound() {
    Procedure found = procedureRepository.findById(99999).orElse(null);
    assertNull(found);
}

@Test
void testNoBlankNamesSaved() {
    List<Procedure> result = procedureRepository.findProceduresWithBlankName();
    assertTrue(result.isEmpty());
}

// @Test
// void testSaveProcedure_DuplicateCode_ShouldThrow() {
//     Procedure duplicate = new Procedure();
//     duplicate.setCode(55559); 
//     duplicate.setName("Eye Surgery");
//     duplicate.setCost(5000.0);

//     assertThrows(DataIntegrityViolationException.class, () -> {
//         procedureRepository.saveAndFlush(duplicate);
//     });
// }



}
