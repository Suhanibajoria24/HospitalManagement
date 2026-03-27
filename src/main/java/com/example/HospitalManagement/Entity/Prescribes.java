package com.example.HospitalManagement.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Prescribes")
@IdClass(PrescribesId.class)
public class Prescribes {

    @Id
    @Column(name = "Physician")
    private Integer physician;

    @Id
    @Column(name = "Patient")
    private Integer patient;

    @Id
    @Column(name = "Medication")
    private Integer medication;

    @Id
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Date")
    private Date date;

    @Column(name = "Appointment")
    private Integer appointment;

    @NotNull
    @Size(max = 30)
    @Column(name = "Dose", nullable = false, length = 30)
    private String dose;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Physician", referencedColumnName = "EmployeeID", insertable = false, updatable = false)
    private Physician physicianEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Patient", referencedColumnName = "SSN", insertable = false, updatable = false)
    private Patient patientEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Medication", referencedColumnName = "Code", insertable = false, updatable = false)
    private Medication medicationEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Appointment", referencedColumnName = "AppointmentID", insertable = false, updatable = false)
    private Appointment appointmentEntity;
}
