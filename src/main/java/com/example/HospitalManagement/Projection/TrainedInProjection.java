package com.example.HospitalManagement.Projection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import com.example.HospitalManagement.Entity.TrainedIn;
import java.util.Date;

@Projection(name = "viewCertified", types = { TrainedIn.class })
public interface TrainedInProjection {

    PhysicianInfo getPhysicianEntity();

    Date getCertificationDate();
    Date getCertificationExpires();

    @Value("#{target.certificationExpires != null && target.certificationExpires.before(new java.util.Date())}")
    boolean isHasExpired();

    interface PhysicianInfo {
        String getName();
    }
}
