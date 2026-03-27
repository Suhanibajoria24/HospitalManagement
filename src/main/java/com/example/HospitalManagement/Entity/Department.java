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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Department")
public class Department {

    @Id
    @Column(name = "DepartmentID")
    private Integer departmentId;

    @NotNull
    @Size(max = 30)
    @Column(name = "Name", nullable = false, length = 30)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Head", referencedColumnName = "EmployeeID", nullable = false)
    private Physician head;

    @JsonIgnore
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<AffiliatedWith> affiliations;
}

