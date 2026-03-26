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
}