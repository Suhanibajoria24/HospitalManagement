package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.OnCall;
import com.example.HospitalManagement.Entity.OnCallId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path = "oncalls")
public interface OnCallRepository extends JpaRepository<OnCall, OnCallId> {

    @RestResource(path = "byNurse", rel = "byNurse")
    List<OnCall> findByNurse(Integer nurse);

    @RestResource(path = "current", rel = "current")
    @Query("SELECT o FROM OnCall o WHERE o.nurse = :nurse AND CURRENT_TIMESTAMP BETWEEN o.onCallStart AND o.onCallEnd")
    List<OnCall> findCurrentOnCall(@Param("nurse") Integer nurse);
}