package com.example.HospitalManagement.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "physician")
public class Physician {

    @Id
    @Column(name = "EmployeeID")
    private int employeeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false, unique = true)
    private int ssn;

   

   
}
