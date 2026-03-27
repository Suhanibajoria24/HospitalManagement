package com.example.HospitalManagement.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Patient")
public class Patient {

    @Id
    @Column(name = "SSN")
    private Integer ssn;

    @NotNull
    @Size(max = 30)
    @Column(name = "Name", nullable = false, length = 30)
    private String name;

    @NotNull
    @Size(max = 30)
    @Column(name = "Address", nullable = false, length = 30)
    private String address;

    @NotNull
    @Size(max = 30)
    @Column(name = "Phone", nullable = false, length = 30)
    private String phone;

    @NotNull
    @Column(name = "InsuranceID", nullable = false)
    private Integer insuranceID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PCP", referencedColumnName = "EmployeeID", nullable = false)
    private Physician pcp;

   @JsonIgnore
   @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
   private List<Appointment> appointments;

   @JsonIgnore
   @OneToMany(mappedBy = "patientEntity", fetch = FetchType.LAZY)
   private List<Prescribes> prescriptions;

   @JsonIgnore
   @OneToMany(mappedBy = "patientEntity", fetch = FetchType.LAZY)
   private List<Stay> stays;

   @JsonIgnore
   @OneToMany(mappedBy = "patientEntity", fetch = FetchType.LAZY)
   private List<Undergoes> proceduresUndergone;
}