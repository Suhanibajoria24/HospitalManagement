package com.example.HospitalManagement.Repository;






import com.example.HospitalManagement.Entity.AffiliatedWith;
import com.example.HospitalManagement.Entity.AffiliatedWithId;
import com.example.HospitalManagement.Projection.AffiliationProjection; // <-- Add this import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// ADD THE EXCERPT PROJECTION HERE:
@RepositoryRestResource(path = "affiliations", collectionResourceRel = "affiliations", excerptProjection = AffiliationProjection.class)
public interface AffiliatedWithRepository extends JpaRepository<AffiliatedWith, AffiliatedWithId> {

    Page<AffiliatedWith> findByPhysician(@Param("physicianId") Integer physicianId, Pageable pageable);

    Page<AffiliatedWith> findByDepartment(@Param("departmentId") Integer departmentId, Pageable pageable);
    
    Page<AffiliatedWith> findByPhysicianAndPrimaryAffiliationTrue(@Param("physicianId") Integer physicianId, Pageable pageable);
}