package com.example.HospitalManagement.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Undergoes")
@IdClass(UndergoesId.class)
public class Undergoes {

    @Id
    @Column(name = "Patient")
    private Integer patient;

    @Id
    @Column(name = "Procedures")
    private Integer procedures;

    @Id
    @Column(name = "Stay")
    private Integer stay;

    @Id
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DateUndergoes")
    private Date dateUndergoes;

    @NotNull
    @Column(name = "Physician", nullable = false)
    private Integer physician;

    @Column(name = "AssistingNurse")
    private Integer assistingNurse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Patient", referencedColumnName = "SSN", insertable = false, updatable = false)
    private Patient patientEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Procedures", referencedColumnName = "Code", insertable = false, updatable = false)
    private Procedure procedure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Stay", referencedColumnName = "StayID", insertable = false, updatable = false)
    private Stay stayEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Physician", referencedColumnName = "EmployeeID", insertable = false, updatable = false)
    private Physician physicianEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssistingNurse", referencedColumnName = "EmployeeID", insertable = false, updatable = false)
    private Nurse assistingNurseEntity;
}
