package com.example.HospitalManagement;

import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.PhysicianRepository;

import jakarta.transaction.Transactional;

// --- IMPORTANT IMPORT CHANGES ---
// Make sure it is the Spring framework Page, NOT the h2.mvstore Page!
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
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

        // Pass a PageRequest (Page 0, max 5 items)
        Pageable pageable = PageRequest.of(0, 5);
        Page<Physician> found = repo.findByName("nikhil", pageable);

        assertFalse(found.isEmpty());
        
        // Use .getContent() to get the actual list of doctors from the Page
        boolean containsSurgeon = found.getContent().stream()
                .anyMatch(physician -> "surgeon".equals(physician.getPosition()));
        assertTrue(containsSurgeon);
    }

    @Test
    void testGetPhysicianByName_NotFound(){
        Pageable pageable = PageRequest.of(0, 5);
        Page<Physician> p = repo.findByName("nik", pageable); // Fixed syntax error here
        assertTrue(p.isEmpty());
    }

    @Test
    void testGetPhysicianByName_NullValue(){
        Pageable pageable = PageRequest.of(0, 5);
        Page<Physician> p = repo.findByName("", pageable);
        assertTrue(p.isEmpty());
    }

    @Test
    void testGetPhysicianByName_InvalidInput(){
        Pageable pageable = PageRequest.of(0, 5);
        Page<Physician> p = repo.findByName("21323", pageable);
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
        p2.setEmployeeId(505);
        p2.setName("Bob");
        p2.setPosition("Cardiologist");
        p2.setSsn(54321);
        repo.save(p2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Physician> found = repo.findByPosition("Cardiologist", pageable);

        assertFalse(found.isEmpty());
        // Use getTotalElements() to check how many items matched the search across all pages
        assertTrue(found.getTotalElements() >= 2); 
    }

    @Test
    void testFindByPosition_NotFound() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Physician> found = repo.findByPosition("Astronaut", pageable);
        assertTrue(found.isEmpty());
    }

    // --- SSN TESTS REMAIN UNCHANGED (No Pagination) ---

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
        Physician found = repo.findBySsn(111000);
        assertNull(found);
    }
    // You will need to add this import at the top of your file for the Sort feature!
    // import org.springframework.data.domain.Sort;

    @Test
    void testFindByPosition_SortedByName() {
        // 1. Setup: Save doctors out of alphabetical order
        Physician p1 = new Physician();
        p1.setEmployeeId(701);
        p1.setName("Dr. Zebra");
        p1.setPosition("Pediatrician");
        p1.setSsn(11111);
        repo.save(p1);

        Physician p2 = new Physician();
        p2.setEmployeeId(702);
        p2.setName("Dr. Apple");
        p2.setPosition("Pediatrician");
        p2.setSsn(22222);
        repo.save(p2);

        // 2. Execute: Request Page 0, Size 5, SORTED by name (A to Z)
        Pageable sortedPageable = PageRequest.of(0, 5, org.springframework.data.domain.Sort.by("name").ascending());
        Page<Physician> found = repo.findByPosition("Pediatrician", sortedPageable);

        // 3. Assert
        assertFalse(found.isEmpty());
        assertEquals(2, found.getTotalElements());
        
        // Verify Dr. Apple comes BEFORE Dr. Zebra in the list!
        assertEquals("Dr. Apple", found.getContent().get(0).getName());
        assertEquals("Dr. Zebra", found.getContent().get(1).getName());
    }

    @Test
    void testFindByName_PageOutOfBounds() {
        // 1. Setup: Save exactly 1 doctor
        Physician p = new Physician();
        p.setEmployeeId(801);
        p.setName("Dr. Lonely");
        p.setPosition("General");
        p.setSsn(33333);
        repo.save(p);

        // 2. Execute: Ask for Page 50 (which obviously doesn't exist)
        Pageable outOfBoundsPageable = PageRequest.of(50, 10);
        Page<Physician> found = repo.findByName("Dr. Lonely", outOfBoundsPageable);

        // 3. Assert
        // The page content should be completely empty...
        assertTrue(found.getContent().isEmpty());
        // ...BUT Spring should still tell us that 1 record exists in total!
        assertEquals(1, found.getTotalElements());
        assertEquals(1, found.getTotalPages());
    }
}