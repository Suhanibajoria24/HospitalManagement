package com.example.HospitalManagement.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PrescribesId implements Serializable {
    private Integer physician;
    private Integer patient;
    private Integer medication;
    private Date date;
}
