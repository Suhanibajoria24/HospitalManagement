package com.example.HospitalManagement.Entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Medication")
public class Medication {

    @Id
    private Integer code;

    private String name;
    private String brand;
    private String description;
}
