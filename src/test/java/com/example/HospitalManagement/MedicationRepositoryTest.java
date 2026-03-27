package com.example.HospitalManagement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import com.example.HospitalManagement.Entity.Medication;
import com.example.HospitalManagement.Repository.MedicationRepository;

@SpringBootTest
// @ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class MedicationRepositoryTest {
    @Autowired
    private MedicationRepository repository;

    @BeforeEach
    void setUp(){
        // repository.deleteAll();
    }

    // debug test
    @Test
    void debugTest() {
        System.out.println("Count = " + repository.count());
        repository.findAll().forEach(System.out::println);
    }

    // Test case 1 : find by a particular Id
    @Test
    void testFindById_Exists() {

        Medication medication = new Medication();
        medication.setCode(1);
        medication.setName("Procrastin-X");
        medication.setBrand("TestBrand");
        medication.setDescription("Test medication");
        repository.save(medication);

        Optional<Medication> med = repository.findById(1);

        assertTrue(med.isPresent());
        assertEquals("Procrastin-X", med.get().getName());
    }

    // Test case 2 : check if an Id not Exists
    @Test
    void testFindById_NotExists() {
        Optional<Medication> med = repository.findById(9999);
        assertFalse(med.isPresent());
    }

    // Test 3 : get all medications
    @Test
    void testFindAllMedications(){
        List<Medication> med_list = repository.findAll();

        assertFalse(med_list.isEmpty());
    }

    // Test case 4 : findByName Test case
    @Test
    void testFindMedicationByName(){
        List<Medication> medication_list = repository.findByName("Procrastin-X");

        // list should not be Empty
        assertFalse(medication_list.isEmpty());

        // check whether correct name retrieved from DB
        assertEquals("Procrastin-X" , medication_list.get(0).getName());
    }

    // Test 5 : Find By Brand Test Case
    @Test
    void testFindMedicationByBrandName(){
        List<Medication> medication_list = repository.findByBrand("TestBrand");

        // list should not be Empty
        assertFalse(medication_list.isEmpty());

        // check whether correct name retrieved from DB
        assertEquals("TestBrand" , medication_list.get(0).getBrand());
    }

    // Test 6 : save new Medication
    // @Test
    // void testSaveNewMedication(){

    //     // create a Medication object and set values
    //     Medication new_med = new Medication();

    //     new_med.setCode(2);
    //     new_med.setName("NewMed");
    //     new_med.setBrand("NewBrand");

    //     // save
    //     Medication saved = repository.save(new_med);

    //     // check it is not null
    //     assertNotNull(saved);
    //     assertEquals("NewMed", saved.getName());
    // }

}
