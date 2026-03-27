package com.example.HospitalManagement.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Appointment")
public class Appointment {

    @Id
    @Column(name = "AppointmentID")
    private Integer appointmentId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Patient", referencedColumnName = "SSN", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PrepNurse", referencedColumnName = "EmployeeID")
    private Nurse prepNurse;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Physician", referencedColumnName = "EmployeeID", nullable = false)
    private Physician physician;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Starto", nullable = false)
    private Date starto;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Endo", nullable = false)
    private Date endo;

    @NotNull
    @Column(name = "ExaminationRoom", nullable = false, columnDefinition = "TEXT")
    private String examinationRoom;

   @JsonIgnore
   @OneToMany(mappedBy = "appointment", fetch = FetchType.LAZY)
   private List<Prescribes> prescriptions;
}

