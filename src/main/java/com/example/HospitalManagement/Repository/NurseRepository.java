package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.Nurse;
import com.example.HospitalManagement.Projection.NurseProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "nurse",
excerptProjection = NurseProjection.class )
public interface NurseRepository extends JpaRepository<Nurse, Integer> {
}
