package com.example.HospitalManagement.config;

import com.example.HospitalManagement.Projection.OnCallProjection;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class RestConfig {

    @Autowired
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {

        config.getProjectionConfiguration()
                .addProjection(OnCallProjection.class);
    }
}