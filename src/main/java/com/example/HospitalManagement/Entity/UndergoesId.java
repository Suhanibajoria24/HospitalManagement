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
public class UndergoesId implements Serializable {
    private Integer patient;
    private Integer procedures;
    private Integer stay;
    private Date dateUndergoes;
}
