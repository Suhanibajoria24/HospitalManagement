package com.example.HospitalManagement.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Medication")
public class Medication {

    @Id
    @Column(name = "Code")
    private Integer code;

    @NotNull
    @Size(max = 30)
    @Column(name = "Name", nullable = false, length = 30)
    private String name;

    @NotNull
    @Size(max = 30)
    @Column(name = "Brand", nullable = false, length = 30)
    private String brand;

    @NotNull
    @Size(max = 30)
    @Column(name = "Description", nullable = false, length = 30)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "medicationEntity", fetch = FetchType.LAZY)
    private List<Prescribes> prescriptions;
}