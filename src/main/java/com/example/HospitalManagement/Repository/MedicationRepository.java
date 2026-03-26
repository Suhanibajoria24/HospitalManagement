package com.example.HospitalManagement.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.example.HospitalManagement.Entity.Medication;

@RepositoryRestResource
public interface MedicationRepository extends JpaRepository<Medication , Integer> {

}
