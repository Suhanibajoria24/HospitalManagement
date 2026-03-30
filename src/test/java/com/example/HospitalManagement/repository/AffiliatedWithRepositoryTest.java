    package com.example.HospitalManagement.repository;


   


import com.example.HospitalManagement.Entity.AffiliatedWith;
import com.example.HospitalManagement.Entity.Department;
import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.AffiliatedWithRepository;
import com.example.HospitalManagement.Repository.DepartmentRepository;
import com.example.HospitalManagement.Repository.PhysicianRepository;

import jakarta.persistence.EntityManager; // <-- NEW IMPORT

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional 
public class AffiliatedWithRepositoryTest {

    @Autowired
    private AffiliatedWithRepository affiliationRepo;

    @Autowired
    private PhysicianRepository physicianRepo;

    @Autowired
    private DepartmentRepository departmentRepo;

    @Autowired
    private EntityManager entityManager; // <-- ADDED THIS TO CLEAR THE CACHE!

    // --- HELPER METHODS ---
    private Physician createTestPhysician(int id, String name) {
        Physician p = new Physician();
        p.setEmployeeId(id);
        p.setName(name);
        p.setPosition("Attending");
        p.setSsn(id * 100); 
        Physician saved = physicianRepo.save(p);
        physicianRepo.flush(); 
        return saved;
    }

    private Department createTestDepartment(int id, String name, Physician head) {
        Department d = new Department(id, name, head, null);
        Department saved = departmentRepo.save(d);
        departmentRepo.flush(); 
        return saved;
    }

    private AffiliatedWith createAffiliation(Physician doc, Department dept, boolean isPrimary) {
        AffiliatedWith affiliation = new AffiliatedWith();
        affiliation.setPhysician(doc.getEmployeeId());
        affiliation.setDepartment(dept.getDepartmentId());
        affiliation.setPrimaryAffiliation(isPrimary);
        
        AffiliatedWith saved = affiliationRepo.save(affiliation);
        affiliationRepo.flush(); 
        return saved;
    }

    // ==========================================
    // --- TESTS ---
    // ==========================================

    @Test
    void testFindByPhysician() {
        Physician docA = createTestPhysician(85001, "Dr. Multi-Tasker");
        
        Department dept1 = createTestDepartment(95001, "Cardio Wing", docA);
        Department dept2 = createTestDepartment(95002, "Surgery Wing", docA);

        createAffiliation(docA, dept1, true);
        createAffiliation(docA, dept2, false);

        // CLEAR THE CACHE! Forces Hibernate to fetch fresh data and build the Objects
        entityManager.clear(); 

        Pageable pageable = PageRequest.of(0, 5);
        Page<AffiliatedWith> results = affiliationRepo.findByPhysician(85001, pageable);

        assertFalse(results.isEmpty());
        assertEquals(2, results.getTotalElements()); 
        assertEquals("Dr. Multi-Tasker", results.getContent().get(0).getPhysicianEntity().getName());
    }

    @Test
    void testFindByDepartment() {
        Physician docA = createTestPhysician(85003, "Dr. Alice");
        Physician docB = createTestPhysician(85004, "Dr. Bob");
        
        Department mainDept = createTestDepartment(95003, "Neurology", docA);

        createAffiliation(docA, mainDept, true);
        createAffiliation(docB, mainDept, true);

        // CLEAR THE CACHE! 
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 5);
        Page<AffiliatedWith> results = affiliationRepo.findByDepartment(95003, pageable);

        assertFalse(results.isEmpty());
        assertEquals(2, results.getTotalElements()); 
        assertEquals("Neurology", results.getContent().get(0).getDepartmentEntity().getName());
    }

    @Test
    void testFindByPhysicianAndPrimaryAffiliationTrue() {
        Physician docA = createTestPhysician(85005, "Dr. Specialist");
        
        Department primaryDept = createTestDepartment(95004, "Primary Care", docA);
        Department secondaryDept = createTestDepartment(95005, "On-Call Ward", docA);

        createAffiliation(docA, primaryDept, true);
        createAffiliation(docA, secondaryDept, false);

        // CLEAR THE CACHE!
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 5);
        Page<AffiliatedWith> results = affiliationRepo.findByPhysicianAndPrimaryAffiliationTrue(85005, pageable);

        assertFalse(results.isEmpty());
        assertEquals(1, results.getTotalElements()); 
        
        AffiliatedWith primaryAffiliation = results.getContent().get(0);
        assertTrue(primaryAffiliation.getPrimaryAffiliation());
        assertEquals("Primary Care", primaryAffiliation.getDepartmentEntity().getName());
    }
}