package com.example.HospitalManagement;



import com.example.HospitalManagement.Entity.Department;
import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.DepartmentRepository;
import com.example.HospitalManagement.Repository.PhysicianRepository;
import org.junit.jupiter.api.Test; // Crucial for "No test cases found" fix
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
public class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepo;

    @Autowired
    private PhysicianRepository physicianRepo;

    // --- HELPER METHOD ---
    // We must create a doctor first, or MySQL will throw a Foreign Key error
    private Physician createTestPhysician(int id, String name, int ssn) {
        Physician p = new Physician();
        p.setEmployeeId(id);
        p.setName(name);
        p.setPosition("Department Head");
        p.setSsn(ssn);
        return physicianRepo.save(p);
    }

    @Test
    void testFindByHead_EmployeeId_Pagination() {
        // 1. Setup: Create two doctors
        Physician docA = createTestPhysician(1001, "Dr. Alpha", 10001111);
        Physician docB = createTestPhysician(1002, "Dr. Beta", 10002222);

        // Assign 3 departments to Doc A
        departmentRepo.save(new Department(1101, "Alpha Wing 1", docA, null));
        departmentRepo.save(new Department(1102, "Alpha Wing 2", docA, null));
        departmentRepo.save(new Department(1103, "Alpha Wing 3", docA, null));

        // Assign 1 department to Doc B
        departmentRepo.save(new Department(1104, "Beta Wing", docB, null));

        // 2. Execute: Ask for page 0, size 2 for Doctor A
        Pageable pageable = PageRequest.of(0, 2);
        Page<Department> page = departmentRepo.findByHead_EmployeeId(1001, pageable);

        // 3. Assert
        assertNotNull(page);
        assertEquals(3, page.getTotalElements()); // Total matching items in DB
        assertEquals(2, page.getContent().size()); // Limit per page
        assertEquals(2, page.getTotalPages()); // 3 items / 2 per page = 2 pages
        assertEquals(1001, page.getContent().get(0).getHead().getEmployeeId()); // Verify it belongs to Doc A
    }

    @Test
    void testFindByName_Pagination() {
        // 1. Setup
        Physician doc = createTestPhysician(1003, "Dr. Gamma", 10003333);
        
        // Use a highly unique name to avoid reading existing DB records
        String searchName = "Special_Cardiology_Unit_X";
        
        // Save 2 departments with the exact same name
        departmentRepo.save(new Department(1105, searchName, doc, null));
        departmentRepo.save(new Department(1106, searchName, doc, null));

        // 2. Execute
        Pageable pageable = PageRequest.of(0, 5);
        Page<Department> page = departmentRepo.findByName(searchName, pageable);

        // 3. Assert
        assertNotNull(page);
        assertEquals(2, page.getTotalElements());
        assertEquals(searchName, page.getContent().get(0).getName());
    }

    @Test
    void testExistsByHead_EmployeeId() {
        // 1. Setup
        Physician workingDoc = createTestPhysician(1004, "Dr. Working", 10004444);
        Physician lazyDoc = createTestPhysician(1005, "Dr. Lazy", 10005555);

        // Assign a department ONLY to the working doctor
        departmentRepo.save(new Department(1107, "Busy Ward", workingDoc, null));

        // 2. Execute & Assert
        // Should return true because Dr. Working heads a department
        boolean isWorkingDocHead = departmentRepo.existsByHead_EmployeeId(1004);
        assertTrue(isWorkingDocHead);

        // Should return false because Dr. Lazy has no departments
        boolean isLazyDocHead = departmentRepo.existsByHead_EmployeeId(1005);
        assertFalse(isLazyDocHead);
    }
}