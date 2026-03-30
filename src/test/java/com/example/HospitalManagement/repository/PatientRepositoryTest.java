package com.example.HospitalManagement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.PatientRepository;
import com.example.HospitalManagement.Repository.PhysicianRepository;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // Ensures changes are rolled back after each test
public class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PhysicianRepository physicianRepository;

    private Physician savedPhysician;

    @BeforeEach
    public void setUp() {
        // 1. We MUST have a Physician in the DB to satisfy the PCP foreign key
        Physician physician = new Physician();
        physician.setEmployeeId(103);
        physician.setName("Dr. House");
        physician.setPosition("Head of Diagnostics");
        physician.setSsn(111223344);

        savedPhysician = physicianRepository.save(physician);

        Patient p1 = new Patient();
        p1.setSsn(100001);
        p1.setName("John Doe");
        p1.setAddress("123 Baker St");
        p1.setPhone("1234567890");
        p1.setInsuranceID(998877);
        p1.setPcp(savedPhysician);
        patientRepository.save(p1);

        Patient p2 = new Patient();
        p2.setSsn(100002);
        p2.setName("Jane Doe");
        p2.setAddress("456 Baker St");
        p2.setPhone("9876543210");
        p2.setInsuranceID(112233);
        p2.setPcp(savedPhysician);
        patientRepository.save(p2);

        Patient p3 = new Patient();
        p3.setSsn(100003);
        p3.setName("Alice");
        p3.setAddress("789 Baker St");
        p3.setPhone("9000000001");
        p3.setInsuranceID(445566);
        p3.setPcp(savedPhysician);
        patientRepository.save(p3);
    }

    @Test
    @Rollback
    void testSavePatient() {
        // 2. Create a new Patient
        Patient patient = new Patient();
        patient.setSsn(100001);
        patient.setName("John Doe");
        patient.setAddress("123 Baker St");
        patient.setPhone("1234567890");
        patient.setInsuranceID(998877);
        patient.setPcp(savedPhysician); // Link to the saved physician

        // 3. Save and Verify
        Patient saved = patientRepository.save(patient);

        assertNotNull(saved);
        assertEquals(100001, saved.getSsn());
        assertEquals("John Doe", saved.getName());
        assertEquals("Dr. House", saved.getPcp().getName());
    }

    @Test
    @Rollback
    void testFindByNameIgnoreCase() {
        // Seed a patient for searching
        Patient p = new Patient();
        p.setSsn(200002);
        p.setName("VED");
        p.setAddress("Nagpur");
        p.setPhone("9999988888");
        p.setInsuranceID(443322);
        p.setPcp(savedPhysician);
        patientRepository.save(p);

        // Test the custom repository method
        var foundPatients = patientRepository.findByNameIgnoreCase("ved");

        assertTrue(foundPatients.size() > 0);
        assertEquals("VED", foundPatients.get(0).getName());
    }

    @Test
    void testFindByNameIgnoreCase_NonExistingName_ReturnsEmptyList() {
        List<Patient> result = patientRepository.findByNameIgnoreCase("Ghost");

        assertThat(result).isEmpty();
    }

    @Test
    @Rollback
    void testUpdatePatientAddress() {
        // Create and Save
        Patient p = new Patient();
        p.setSsn(300003);
        p.setName("Sita");
        p.setAddress("Pune");
        p.setPhone("7777766666");
        p.setInsuranceID(111111);
        p.setPcp(savedPhysician);
        patientRepository.save(p);

        // Update
        Optional<Patient> optionalPatient = patientRepository.findById(300003);
        assertTrue(optionalPatient.isPresent());

        Patient toUpdate = optionalPatient.get();
        toUpdate.setAddress("Mumbai");
        patientRepository.save(toUpdate);

        // Verify Update
        Patient updated = patientRepository.findById(300003).get();
        assertEquals("Mumbai", updated.getAddress());
    }

    @Test
    void testFindById_ExistingId_ReturnsPatient() {
        assertThat(patientRepository.findById(100001)).isPresent();
    }

    @Test
    void testFindById_NonExistingId_ReturnsEmpty() {
        assertThat(patientRepository.findById(99999999)).isEmpty();
    }

    @Test
    void testFindAll_ReturnsAllPatients() {
        assertThat(patientRepository.findAll()).isNotEmpty();
    }
}