package com.example.HospitalManagement.Repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.HospitalManagement.Entity.Appointment;

@RepositoryRestResource(path = "appointments")
public interface AppointmentRepository extends JpaRepository<Appointment, Integer>{

    List<Appointment> findByStartoBetween(Date start, Date end);

    List<Appointment> findByPatientName(String name);

}
