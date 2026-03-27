package com.example.HospitalManagement.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.validation.annotation.Validated;

import com.example.HospitalManagement.Entity.Patient;

@RepositoryRestResource(path = "patients")
@Validated
public interface PatientRepository extends JpaRepository<Patient, Integer>{

    List<Patient> findByNameIgnoreCase(String name);
}
