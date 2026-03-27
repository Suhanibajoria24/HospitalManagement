package com.example.HospitalManagement.Entity;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Affiliated_With")
@IdClass(AffiliatedWithId.class)
public class AffiliatedWith {

    @Id
    @Column(name = "Physician")
    private Integer physician;

    @Id
    @Column(name = "Department")
    private Integer department;

    @NotNull
    @Column(name = "PrimaryAffiliation", nullable = false)
    private Boolean primaryAffiliation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Physician", referencedColumnName = "EmployeeID", insertable = false, updatable = false)
    private Physician physicianEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Department", referencedColumnName = "DepartmentID", insertable = false, updatable = false)
    private Department departmentEntity;
}

