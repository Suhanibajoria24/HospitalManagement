package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.Physician;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "allPhysician", collectionResourceRel = "physicians")
public interface PhysicianRepository extends JpaRepository<Physician,Integer> {
    Optional<Physician> findByName(String name);

    List<Physician> findByPosition(String position);

    Physician findBySsn(int ssn);


//    Page<Physician> findByPosition(String position, Pageable pageable);
}
