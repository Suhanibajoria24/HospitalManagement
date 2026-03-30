package com.example.HospitalManagement.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Trained_In")
@IdClass(TrainedInId.class)
public class TrainedIn {

    @Id
    @Column(name = "Physician")
    private Integer physician;

    @Id
    @Column(name = "Treatment")
    private Integer treatment;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CertificationDate", nullable = false)
    private Date certificationDate;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CertificationExpires", nullable = false)
    private Date certificationExpires;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Physician", referencedColumnName = "EmployeeID", insertable = false, updatable = false)
    private Physician physicianEntity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Treatment", referencedColumnName = "Code", insertable = false, updatable = false)
    private Procedure treatmentEntity;
}
