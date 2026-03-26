package com.example.HospitalManagement;

import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.PhysicianRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
        p.setName("nikhil");
        p.setPosition("surgeon");
        p.setSsn(93224);

        repo.save(p);

        Optional<Physician> found = repo.findByName("nikhil");

        assertTrue(found.isPresent());
        assertEquals("surgeon", found.get().getPosition());
    }

    @Test
    void testGetPhysicianByName_NotFound(){
        Optional<Physician> p = repo.findByName("nik");
        assertFalse(p.isPresent());
    }

    @Test
    void testGetPhysicianByName_NullValue(){
        Optional<Physician> p = repo.findByName("");
        assertFalse(p.isPresent());
    }

    @Test
    void testGetPhysicianByName_InvalidInput(){
        Optional<Physician> p = repo.findByName("21323");
        assertFalse(p.isPresent());
    }
}