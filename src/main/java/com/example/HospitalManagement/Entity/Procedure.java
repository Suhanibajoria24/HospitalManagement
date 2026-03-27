package com.example.HospitalManagement.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Procedures")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Procedure {
    @Id
    @Column(name = "Code")
    private Integer code;

    @NotBlank
    @Size(max = 30)
    @Column(name = "Name", length = 30, nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "Cost", nullable = false)
    private Double cost;

    @JsonIgnore
    @OneToMany(mappedBy = "treatment", fetch = FetchType.LAZY)
    private List<TrainedIn> trainedPhysicians;

    @JsonIgnore
    @OneToMany(mappedBy = "procedure", fetch = FetchType.LAZY)
    private List<Undergoes> undergoes;    
}
