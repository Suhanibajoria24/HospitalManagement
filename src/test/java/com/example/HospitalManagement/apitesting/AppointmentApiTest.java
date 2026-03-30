package com.example.HospitalManagement.apitesting;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.AppointmentRepository;
import com.example.HospitalManagement.Repository.PatientRepository;
import com.example.HospitalManagement.Repository.PhysicianRepository;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AppointmentApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PhysicianRepository physicianRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // ── Seed ─────────────────────────────────────────────────

    @BeforeEach
    void setup() {
        if (!physicianRepository.existsById(200)) {
            Physician doc = new Physician();
            doc.setEmployeeId(200);
            doc.setName("Dr. Test");
            doc.setPosition("Surgeon");
            doc.setSsn(777777777);
            physicianRepository.save(doc);
        }

        if (!patientRepository.existsById(900000001)) {
            Patient patient = new Patient();
            patient.setSsn(900000001);
            patient.setName("TestPatient");
            patient.setAddress("Nagpur");
            patient.setPhone("9000000001");
            patient.setInsuranceID(11111);
            patient.setPcp(physicianRepository.findById(200).get());
            patientRepository.save(patient);
        }
    }

    @AfterEach
    void cleanup() {
        // FK order: appointments first, then patient, then physician
        appointmentRepository.deleteAll();
        if (patientRepository.existsById(900000001))
            patientRepository.deleteById(900000001);
        if (physicianRepository.existsById(200))
            physicianRepository.deleteById(200);
    }

    //GET appointments

    @Test
    void testGetAllAppointments_ValidPagination_ReturnsPagedData() throws Exception {
        mockMvc.perform(get("/appointments?page=0&size=5"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void testGetAllAppointments_PageOutOfRange_ReturnsOk() throws Exception {
        mockMvc.perform(get("/appointments?page=10&size=5"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    
    //book appointments

    @Test
    void testCreateAppointment_ValidData_ReturnsCreated() throws Exception {
        String json = """
        {
            "appointmentId": 5001,
            "patient": "/patients/900000001",
            "physician": "/allPhysician/200",
            "prepNurse": null,
            "starto": "2026-06-01T10:00:00",
            "endo": "2026-06-01T11:00:00",
            "examinationRoom": "Room A"
        }
        """;

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    void testCreateAppointment_InvalidPatientId_ReturnsNotFound() throws Exception {
        String json = """
        {
            "appointmentID": 5002,
            "patient": "/patients/999999999",
            "physician": "/allPhysician/200",
            "prepNurse": null,
            "start": "2026-06-01T10:00:00",
            "end": "2026-06-01T11:00:00",
            "examinationRoom": "Room B"
        }
        """;

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAppointment_PastDate_ReturnsBadRequest() throws Exception {
        String json = """
        {
            "appointmentID": 5003,
            "patient": "/patients/900000001",
            "physician": "/allPhysician/200",
            "prepNurse": null,
            "start": "2020-01-01T10:00:00",
            "end": "2020-01-01T11:00:00",
            "examinationRoom": "Room C"
        }
        """;

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAppointment_MissingFields_ReturnsBadRequest() throws Exception {
        String json = """
        {
            "appointmentID": 5004
        }
        """;

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

}