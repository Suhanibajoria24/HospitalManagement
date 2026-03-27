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

import org.springframework.data.domain.Persistable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Medication")
public class Medication implements Persistable<Integer>{

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


    // override methods
    @Override
    public Integer getId() {
        // TODO Auto-generated method stub
        return this.code;
    }

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        // TODO Auto-generated method stub
        return true;
    }  
}