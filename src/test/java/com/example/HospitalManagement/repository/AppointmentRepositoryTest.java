package com.example.HospitalManagement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.example.HospitalManagement.Entity.Appointment;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.AppointmentRepository;
import com.example.HospitalManagement.Repository.PatientRepository;
import com.example.HospitalManagement.Repository.PhysicianRepository;

@SpringBootTest
@Transactional
public class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PhysicianRepository physicianRepository;

    private Physician savedPhysician;
    private Patient savedPatient;

    @BeforeEach
    public void setUp() {
        // Physician
        Physician physician = new Physician();
        physician.setEmployeeId(103);
        physician.setName("Dr. House");
        physician.setPosition("Head of Diagnostics");
        physician.setSsn(111223344);
        savedPhysician = physicianRepository.save(physician);

        // Patient
        Patient patient = new Patient();
        patient.setSsn(100001);
        patient.setName("John Doe");
        patient.setAddress("123 Baker St");
        patient.setPhone("1234567890");
        patient.setInsuranceID(998877);
        patient.setPcp(savedPhysician);
        savedPatient = patientRepository.save(patient);
    }

    //Save

    @Test
    @Rollback
    void testSaveAppointment() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(6001);
        appointment.setPatient(savedPatient);
        appointment.setPhysician(savedPhysician);
        appointment.setStarto(new Date(126, 5, 1, 10, 0, 0)); // 2026-06-01 10:00
        appointment.setEndo(new Date(126, 5, 1, 11, 0, 0));   // 2026-06-01 11:00
        appointment.setExaminationRoom("Room A");

        Appointment saved = appointmentRepository.save(appointment);

        assertNotNull(saved);
        assertEquals(6001, saved.getAppointmentId());
        assertEquals("John Doe", saved.getPatient().getName());
        assertEquals("Dr. House", saved.getPhysician().getName());
        assertEquals("Room A", saved.getExaminationRoom());
    }

    //findByStartoBetween

   @Test
@Rollback
void testFindByDate_MatchingDate_ReturnsAppointments() {
    Appointment appointment = new Appointment();
    appointment.setAppointmentId(6002);
    appointment.setPatient(savedPatient);
    appointment.setPhysician(savedPhysician);
    appointment.setStarto(new Date(126, 5, 1, 10, 0, 0)); // 2026-06-01
    appointment.setEndo(new Date(126, 5, 1, 11, 0, 0));
    appointment.setExaminationRoom("Room B");
    appointmentRepository.save(appointment);

    List<Appointment> result = appointmentRepository.findByStartoBetween(
        new Date(126, 5, 1, 0, 0, 0),   // 2026-06-01 start
        new Date(126, 5, 1, 23, 59, 59)  // 2026-06-01 end
    );

    // ✅ just verify our saved appointment is present in results
    assertTrue(result.size() > 0);
    assertTrue(result.stream()
        .anyMatch(a -> a.getAppointmentId() == 6002));
}

    @Test
    @Rollback
    void testFindByDate_NoMatchingDate_ReturnsEmptyList() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(6003);
        appointment.setPatient(savedPatient);
        appointment.setPhysician(savedPhysician);
        appointment.setStarto(new Date(126, 5, 1, 10, 0, 0)); // 2026-06-01
        appointment.setEndo(new Date(126, 5, 1, 11, 0, 0));
        appointment.setExaminationRoom("Room C");
        appointmentRepository.save(appointment);

        // Search different date
        List<Appointment> result = appointmentRepository.findByStartoBetween(
            new Date(126, 0, 1, 0, 0, 0),   // 2026-01-01 start
            new Date(126, 0, 1, 23, 59, 59)  // 2026-01-01 end
        );

        assertThat(result).isEmpty();
    }

    //findByPatientName

    @Test
    @Rollback
    void testFindByPatientName_ExistingName_ReturnsAppointments() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(6004);
        appointment.setPatient(savedPatient);
        appointment.setPhysician(savedPhysician);
        appointment.setStarto(new Date(126, 5, 1, 10, 0, 0));
        appointment.setEndo(new Date(126, 5, 1, 11, 0, 0));
        appointment.setExaminationRoom("Room D");
        appointmentRepository.save(appointment);

        List<Appointment> result = appointmentRepository.findByPatientName("John Doe");

        assertTrue(result.size() > 0);
        assertEquals("John Doe", result.get(0).getPatient().getName());
    }

    @Test
    void testFindByPatientName_NonExistingName_ReturnsEmptyList() {
        List<Appointment> result = appointmentRepository.findByPatientName("Ghost Patient");

        assertThat(result).isEmpty();
    }

    //findById

    @Test
    @Rollback
    void testFindById_ExistingId_ReturnsAppointment() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(6005);
        appointment.setPatient(savedPatient);
        appointment.setPhysician(savedPhysician);
        appointment.setStarto(new Date(126, 5, 1, 10, 0, 0));
        appointment.setEndo(new Date(126, 5, 1, 11, 0, 0));
        appointment.setExaminationRoom("Room E");
        appointmentRepository.save(appointment);

        Optional<Appointment> found = appointmentRepository.findById(6005);

        assertTrue(found.isPresent());
        assertEquals("Room E", found.get().getExaminationRoom());
    }

    @Test
    void testFindById_NonExistingId_ReturnsEmpty() {
        assertThat(appointmentRepository.findById(99999)).isEmpty();
    }

    //findAll

    @Test
    @Rollback
    void testFindAll_ReturnsAllAppointments() {
        Appointment a1 = new Appointment();
        a1.setAppointmentId(6006);
        a1.setPatient(savedPatient);
        a1.setPhysician(savedPhysician);
        a1.setStarto(new Date(126, 5, 1, 10, 0, 0));
        a1.setEndo(new Date(126, 5, 1, 11, 0, 0));
        a1.setExaminationRoom("Room F");
        appointmentRepository.save(a1);

        Appointment a2 = new Appointment();
        a2.setAppointmentId(6007);
        a2.setPatient(savedPatient);
        a2.setPhysician(savedPhysician);
        a2.setStarto(new Date(126, 6, 1, 10, 0, 0));
        a2.setEndo(new Date(126, 6, 1, 11, 0, 0));
        a2.setExaminationRoom("Room G");
        appointmentRepository.save(a2);

        assertThat(appointmentRepository.findAll().size()).isGreaterThanOrEqualTo(2);
    }
}