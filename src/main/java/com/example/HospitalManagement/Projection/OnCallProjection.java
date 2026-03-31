package com.example.HospitalManagement.Projection;

import com.example.HospitalManagement.Entity.OnCall;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "onCallView", types = OnCall.class)
public interface OnCallProjection {

    Integer getBlockFloor();
    Integer getBlockCode();

    @Value("#{target.nurseEntity.name}")
    String getNurseName();
}