package com.example.HospitalManagement.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="procedures")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Procedure {
    @Id
    @Column(name = "code")
    private Integer code;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "cost", nullable = false)
    private Double cost;
}
