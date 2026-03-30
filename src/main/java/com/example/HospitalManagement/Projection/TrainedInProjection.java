package com.example.HospitalManagement.Projection;

import org.springframework.data.rest.core.config.Projection;
import com.example.HospitalManagement.Entity.TrainedIn;
import java.util.Date;

@Projection(name = "viewCertified", types = { TrainedIn.class })
public interface TrainedInProjection {

    PhysicianInfo getPhysicianEntity();

    Date getCertificationDate();
    Date getCertificationExpires();

    default boolean isHasExpired() {
        return getCertificationExpires() != null &&
               getCertificationExpires().before(new Date());
    }

    interface PhysicianInfo {
        String getName();
    }
}
