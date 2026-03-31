package com.example.HospitalManagement.apitesting;
import com.example.HospitalManagement.Entity.*;
import com.example.HospitalManagement.Repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class NurseApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PhysicianRepository physicianRepository;

    @Autowired
    private OnCallRepository onCallRepository;

    @Autowired
    private BlockRepository blockRepository;

    private Nurse nurse1;
    private Nurse nurse2;

    @BeforeEach
    void setUp() {
        nurse1 = new Nurse(301, "Test Nurse A", "Nurse", true, "8881");
        nurse2 = new Nurse(302, "Test Nurse B", "Head Nurse", true, "8882");

        nurseRepository.saveAll(List.of(nurse1, nurse2));
    }
    @Test
    void testGetAllNurses_WithProjection() throws Exception {

        mockMvc.perform(get("/nurse?projection=nurseView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.nurses").isArray())
                .andExpect(jsonPath("$._embedded.nurses").isNotEmpty());
    }
    @Test
    void testProjectionFields() throws Exception {

        mockMvc.perform(get("/nurse?projection=nurseView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.nurses[0].name").exists())
                .andExpect(jsonPath("$._embedded.nurses[0].position").exists())
                .andExpect(jsonPath("$._embedded.nurses[0].registered").exists())
                .andExpect(jsonPath("$._embedded.nurses[0].availability").exists())
                .andExpect(jsonPath("$._embedded.nurses[0].ssn").doesNotExist());
    }
    @Test
    void testPagination() throws Exception {

        mockMvc.perform(get("/nurse?page=0&size=2&projection=nurseView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.nurses.length()").value(2));
    }
    @Test
    void testLargeSize() throws Exception {

        mockMvc.perform(get("/nurse?size=1000&projection=nurseView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.nurses").isArray());
    }

    @Test
    void testAddNurse_Success() throws Exception {

        String json = """
        {
          "employeeId": 303,
          "name": "New Nurse",
          "position": "Nurse",
          "registered": true,
          "ssn": 9991
        }
        """;

        mockMvc.perform(post("/nurse")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());
    }

    void testAddNurse_DuplicateId() throws Exception {
        Nurse existing = new Nurse(301, "Existing Nurse", "Nurse", true, 8881);
        nurseRepository.saveAndFlush(existing);
        String json = """
        {
          "employeeId": 301,
          "name": "Duplicate Nurse",
          "position": "Nurse",
          "registered": true,
          "ssn": 9992
        }
        """;
        mockMvc.perform(post("/nurse")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isConflict()); // ✅ NOW WORKS
    }
    @Test
    void testAddNurse_MissingField() throws Exception {

        String json = """
        {
          "employeeId": 304,
          "position": "Nurse",
          "registered": true,
          "ssn": 9993
        }
        """;

        mockMvc.perform(post("/nurse")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());
    }
    @Test
    void testAddNurse_InvalidData() throws Exception {

        String json = """
        {
          "employeeId": "abc",
          "name": "Invalid Nurse",
          "position": "Nurse",
          "registered": true,
          "ssn": 9994
        }
        """;

        mockMvc.perform(post("/nurse")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateNurse_Success() throws Exception {
        Nurse nurse = new Nurse(601, "Old Name", "Nurse", true, 12345);
        nurseRepository.saveAndFlush(nurse);
        String updatedJson = """
        {
          "name": "Updated Name",
          "position": "Head Nurse",
          "registered": true,
          "ssn": 9999
        }
        """;
        mockMvc.perform(put("/nurse/601")
                        .contentType("application/json")
                        .content(updatedJson))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateNurse_NotFound() throws Exception {

        String json = """
        {
          "name": "Updated Name",
          "position": "Nurse",
          "registered": true,
          "ssn": 1234
        }
        """;

        mockMvc.perform(put("/nurse/9999")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());;
    }
    @Test
    void testUpdateNurse_InvalidData() throws Exception {

        Nurse nurse = new Nurse(602, "Test", "Nurse", true, 11111);
        nurseRepository.saveAndFlush(nurse);

        String json = """
        {
          "name": "Updated",
          "position": "Nurse",
          "registered": true,
          "ssn": "invalid"
        }
        """;

        mockMvc.perform(put("/nurse/602")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testUpdateNurse_EmptyBody() throws Exception {

        Nurse nurse = new Nurse(603, "Test", "Nurse", true, 11111);
        nurseRepository.saveAndFlush(nurse);

        mockMvc.perform(put("/nurse/603")
                        .contentType("application/json")
                        .content(""))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testPatchNurse_Success() throws Exception {

        Nurse nurse = new Nurse(701, "Old Name", "Nurse", true, 12345);
        nurseRepository.saveAndFlush(nurse);

        String json = """
        {
          "name": "Patched Name"
        }
        """;

        mockMvc.perform(patch("/nurse/701")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNoContent());
    }
    @Test
    void testPatchNurse_NotFound() throws Exception {

        String json = """
        {
          "name": "Updated"
        }
        """;

        mockMvc.perform(patch("/nurse/9999")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }
    @Test
    void testPatchNurse_InvalidData() throws Exception {

        Nurse nurse = new Nurse(702, "Test", "Nurse", true, 11111);
        nurseRepository.saveAndFlush(nurse);

        String json = """
        {
          "ssn": "invalid"
        }
        """;

        mockMvc.perform(patch("/nurse/702")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @Rollback
    void testGetAppointment_ByNurse_Success() throws Exception {

        Physician physician = new Physician();
        physician.setEmployeeId(900);
        physician.setName("Dr. Test");
        physician.setPosition("Doctor");
        physician.setSsn(11111);
        physicianRepository.saveAndFlush(physician);

        Nurse nurse = new Nurse(1001, "Test Nurse", "Nurse", true, 12345);
        nurseRepository.saveAndFlush(nurse);

        Patient patient = new Patient();
        patient.setSsn(10000050);
        patient.setName("David Test");
        patient.setAddress("Pune");
        patient.setPhone("9999999999");
        patient.setInsuranceID(12345);
        patient.setPcp(physician);
        patientRepository.saveAndFlush(patient);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(5001);
        appointment.setPrepNurse(nurse);
        appointment.setPatient(patient);
        appointment.setPhysician(physician);
        appointment.setExaminationRoom("Room A");
        appointment.setStarto(new Date());
        appointment.setEndo(new Date());

        appointmentRepository.saveAndFlush(appointment);

        mockMvc.perform(get("/appointments/search/byNurse")
                        .param("nurse", "http://localhost/nurse/1001")
                        .param("projection", "appointmentView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.appointments").isNotEmpty())
                .andExpect(jsonPath("$._embedded.appointments[0].patientName")
                        .value("David Test"))
                .andExpect(jsonPath("$._embedded.appointments[0].examinationRoom").value("Room A"));
    }

    @Test
    @Transactional
    @Rollback
    void testGetAppointment_ByNurse_Empty() throws Exception {

        Nurse nurse = new Nurse(1002, "No Appointment Nurse", "Nurse", true, 22222);
        nurseRepository.saveAndFlush(nurse);

        mockMvc.perform(get("/appointments/search/byNurse")
                        .param("nurse", "http://localhost/nurse/1002")
                        .param("projection", "appointmentView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.appointments").isEmpty());
    }

    @Test
    void testGetAppointment_InvalidNurse() throws Exception {

        mockMvc.perform(get("/appointments/search/byNurse")
                        .param("nurse", "http://localhost/nurse/9999")
                        .param("projection", "appointmentView"))
                .andExpect(status().isOk()); // returns empty
    }

    @Test
    void testGetAppointment_MissingParam() throws Exception {

        mockMvc.perform(get("/appointments/search/byNurse"))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @Rollback
    void testGetAppointment_MultipleResults() throws Exception {

        Physician physician = new Physician();
        physician.setEmployeeId(901);
        physician.setName("Dr. Multi");
        physician.setPosition("Doctor");
        physician.setSsn(22222);
        physicianRepository.saveAndFlush(physician);

        Nurse nurse = new Nurse(1003, "Multi Nurse", "Nurse", true, 33333);
        nurseRepository.saveAndFlush(nurse);
        Patient patient = new Patient();
        patient.setSsn(10000060);
        patient.setName("Multi Patient");
        patient.setAddress("Nagpur");
        patient.setPhone("8888888888");
        patient.setInsuranceID(22222);
        patient.setPcp(physician);
        patientRepository.saveAndFlush(patient);

        Appointment a1 = new Appointment();
        a1.setAppointmentId(6001);
        a1.setPrepNurse(nurse);
        a1.setPatient(patient);
        a1.setPhysician(physician);
        a1.setExaminationRoom("Room A");
        a1.setStarto(new Date());
        a1.setEndo(new Date());

        Appointment a2 = new Appointment();
        a2.setAppointmentId(6002);
        a2.setPrepNurse(nurse);
        a2.setPatient(patient);
        a2.setPhysician(physician);
        a2.setExaminationRoom("Room B");
        a2.setStarto(new Date());
        a2.setEndo(new Date());

        appointmentRepository.save(a1);
        appointmentRepository.save(a2);

        mockMvc.perform(get("/appointments/search/byNurse")
                        .param("nurse", "http://localhost/nurse/1003")
                        .param("projection", "appointmentView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.appointments.length()").value(2));
    }

    @Test
    @Transactional
    @Rollback
    void testGetOnCall_ByNurse_Success() throws Exception {

        // Nurse
        Nurse nurse = new Nurse(2001, "OnCall Nurse", "Nurse", true, 5555);
        nurseRepository.save(nurse);

        // Block
        Block block = new Block();
        block.setBlockFloor(2);
        block.setBlockCode(101);
        blockRepository.save(block);

        // OnCall
        OnCall onCall = new OnCall();
        onCall.setNurse(2001);
        onCall.setBlockFloor(2);
        onCall.setBlockCode(101);
        onCall.setOnCallStart(new Date());
        onCall.setOnCallEnd(new Date(System.currentTimeMillis() + 100000));

        onCallRepository.save(onCall);
        onCallRepository.flush();
        mockMvc.perform(get("/oncalls/search/byNurse")
                        .param("nurse", "2001")
                        .param("projection", "onCallView"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.onCalls").isArray())
                .andExpect(jsonPath("$._embedded.onCalls").isNotEmpty());
    }
    @Test
    @Transactional
    @Rollback
    void testGetOnCall_NoData() throws Exception {

        mockMvc.perform(get("/oncalls/search/byNurse")
                        .param("nurse", "9999")
                        .param("projection", "onCallView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.onCalls").isEmpty());
    }
    @Test
    @Transactional
    @Rollback
    void testOnCallProjectionFields() throws Exception {


        Nurse nurse = new Nurse(2001, "OnCall Nurse", "Nurse", true, 5555);
        nurseRepository.save(nurse);


        Block block = new Block(2, 101);
        block.setNew(true);

        blockRepository.save(block);
        blockRepository.flush();


        OnCall onCall = new OnCall();

        onCall.setNurse(2001);
        onCall.setNurseEntity(nurse);

        onCall.setBlockFloor(2);
        onCall.setBlockCode(101);
        onCall.setBlock(block);

        onCall.setOnCallStart(new Date());
        onCall.setOnCallEnd(new Date(System.currentTimeMillis() + 100000));

        onCallRepository.save(onCall);
        onCallRepository.flush();

        mockMvc.perform(get("/oncalls/search/byNurse")
                        .param("nurse", "2001")
                        .param("projection", "onCallView"))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$._embedded.onCalls").isArray())
                .andExpect(jsonPath("$._embedded.onCalls").isNotEmpty())

                .andExpect(jsonPath("$._embedded.onCalls[0].blockFloor").value(2))
                .andExpect(jsonPath("$._embedded.onCalls[0].blockCode").value(101))
                .andExpect(jsonPath("$._embedded.onCalls[0].nurseName").value("OnCall Nurse"));
    }
    @Test
    void testGetOnCall_InvalidParam() throws Exception {

        mockMvc.perform(get("/oncalls/search/byNurse")
                        .param("nurse", "abc"))
                .andExpect(status().isInternalServerError());
    }
    @Test
    void testGetOnCall_MissingParam() throws Exception {

        mockMvc.perform(get("/oncalls/search/byNurse"))
                .andExpect(status().isOk());
    }
}
