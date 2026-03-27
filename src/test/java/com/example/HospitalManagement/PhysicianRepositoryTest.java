package com.example.HospitalManagement;

import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.PhysicianRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PhysicianRepositoryTest {

    @Autowired
    private PhysicianRepository repo;

    @Test
    void testGetPhysicianByName_success(){
        Physician p = new Physician();
        p.setEmployeeId(12);
        p.setName("nikhil");
        p.setPosition("surgeon");
        p.setSsn(93224);

        repo.save(p);

        List<Physician> found = repo.findByName("nikhil");

        assertFalse(found.isEmpty());
        // Check if the one we just saved is in the list
        boolean containsSurgeon = found.stream().anyMatch(physician -> "surgeon".equals(physician.getPosition()));
        assertTrue(containsSurgeon);
    }

    @Test
    void testGetPhysicianByName_NotFound(){
        List<Physician> p = repo.findByName("nik");
        assertTrue(p.isEmpty());
    }
    
    

    @Test
    void testGetPhysicianByName_NullValue(){
        List<Physician> p = repo.findByName("");
        assertTrue(p.isEmpty());
    }

    @Test
    void testGetPhysicianByName_InvalidInput(){
        List<Physician> p = repo.findByName("21323");
        assertTrue(p.isEmpty());
    }

    @Test
    void testFindByPosition_Success() {
        Physician p1 = new Physician();
        p1.setEmployeeId(101);
        p1.setName("Alice");
        p1.setPosition("Cardiologist");
        p1.setSsn(12345);
        repo.save(p1);

        Physician p2 = new Physician();
        p2.setEmployeeId(102);
        p2.setName("Bob");
        p2.setPosition("Cardiologist");
        p2.setSsn(54321);
        repo.save(p2);

        List<Physician> found = repo.findByPosition("Cardiologist");

        assertFalse(found.isEmpty());
        // We saved at least 2 Cardiologists, so the list should have at least 2 items
        assertTrue(found.size() >= 2); 
    }

    @Test
    void testFindByPosition_NotFound() {
        List<Physician> found = repo.findByPosition("Astronaut");
        assertTrue(found.isEmpty());
    }


    @Test
    void testFindBySsn_Success() {
        Physician p = new Physician();
        p.setEmployeeId(200);
        p.setName("Charlie");
        p.setPosition("Neurologist");
        p.setSsn(999888);
        repo.save(p);

        Physician found = repo.findBySsn(999888);

        assertNotNull(found);
        assertEquals("Charlie", found.getName());
        assertEquals("Neurologist", found.getPosition());
    }

    @Test
    void testFindBySsn_NotFound() {
        // Because the return type is 'Physician', Spring Data returns null if not found
        Physician found = repo.findBySsn(111000);
        assertNull(found);
    }
}