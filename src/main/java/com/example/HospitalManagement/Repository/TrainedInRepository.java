package com.example.HospitalManagement.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.example.HospitalManagement.Entity.TrainedIn;
import com.example.HospitalManagement.Entity.TrainedInId;
import com.example.HospitalManagement.Projection.TrainedInProjection;

@RepositoryRestResource(path = "trainedIn", collectionResourceRel = "trainedIns",excerptProjection = TrainedInProjection.class)
public interface TrainedInRepository extends JpaRepository<TrainedIn, TrainedInId> {

    @RestResource(path = "findByTreatment")
    Page<TrainedIn> findByTreatment(@Param("treatment") Integer treatment, Pageable pageable);

    @RestResource(path = "findByPhysicianAndTreatment")
    Page<TrainedIn> findByPhysicianAndTreatment(
        @Param("physician") Integer physician,
        @Param("treatment") Integer treatment,
        Pageable pageable
    );

}