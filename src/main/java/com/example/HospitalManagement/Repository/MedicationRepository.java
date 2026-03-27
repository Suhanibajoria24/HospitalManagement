package com.example.HospitalManagement.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.example.HospitalManagement.Entity.Medication;

@RepositoryRestResource(path = "allMedications" , collectionResourceRel = "medications")
public interface MedicationRepository extends JpaRepository<Medication , Integer> {
     List<Medication> findByName(String name);
     List<Medication> findByBrand(String brandName);
}
