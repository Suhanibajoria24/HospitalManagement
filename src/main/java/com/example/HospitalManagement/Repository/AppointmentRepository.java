package com.example.HospitalManagement.Repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.example.HospitalManagement.Entity.Appointment;

@RepositoryRestResource(path = "appointments")
public interface AppointmentRepository extends JpaRepository<Appointment, Integer>{

    @RestResource(path = "date", rel = "date")
    List<Appointment> findByStartoBetween(
        @Param("start") Date start,
        @Param("end") Date end
    );

    // Find by patient name — navigates to Patient entity
    @RestResource(path = "patientName", rel = "patientName")
    List<Appointment> findByPatientName(@Param("name") String name);

}
