package com.example.HospitalManagement.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.HospitalManagement.Entity.Patient;

@RepositoryRestResource(path = "patients")
public interface PatientRepository extends JpaRepository<Patient, Integer>{

}
