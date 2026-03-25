package com.example.HospitalManagement.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode
public class BlockId implements Serializable {
    private Integer blockFloor;
    private Integer blockCode;

}