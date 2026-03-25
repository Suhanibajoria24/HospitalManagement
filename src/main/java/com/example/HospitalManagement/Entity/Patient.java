package com.example.HospitalManagement.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "patient")
public class Patient {

    @Id
    @Column(name = "SSN")
    private Integer ssn;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Address")
    private String address;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "InsuranceID")
    private Integer insuranceID;

    //Many patients → one physician (PCP)
    // @ManyToOne
    // @JoinColumn(name = "PCP", referencedColumnName = "EmployeeID")
    // private Physician pcp;

    
}
