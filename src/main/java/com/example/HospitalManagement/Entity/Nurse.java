package com.example.HospitalManagement.Entity;


import jakarta.persistence.*;

@Entity
@Table(name="nurse")
public class Nurse {

    @Id
    @Column(name="EmployeeId")
    private int employeeId;

    @Column(name = "Name", nullable = false, length = 30)
    private String name;

    @Column(name = "Position", length = 30)
    private String position;

    @Column(name = "Registered")
    private boolean registered;

    @Column(name = "SSN", unique = true)
    private String ssn;

    public Nurse() {}
    public Nurse(int employeeId, String name, String position, boolean registered, String ssn) {
        this.employeeId = employeeId;
        this.name = name;
        this.position = position;
        this.registered = registered;
        this.ssn = ssn;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    @Transient
    public String getAvailability() {
        return "AVAILABLE";
    }

}
