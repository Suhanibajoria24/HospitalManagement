package com.example.HospitalManagement.Repository;



import com.example.HospitalManagement.Entity.Physician;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "allPhysician", collectionResourceRel = "physicians")
public interface PhysicianRepository extends JpaRepository<Physician, Integer> {

    // 1. Paginated search by Name
    Page<Physician> findByName(@Param("name") String name, Pageable pageable);

    // 2. Paginated search by Position (Uncommented and fixed!)
    Page<Physician> findByPosition(@Param("position") String position, Pageable pageable);

    // 3. Search by SSN (No pagination needed here!)
    Physician findBySsn(@Param("ssn") int ssn);
}