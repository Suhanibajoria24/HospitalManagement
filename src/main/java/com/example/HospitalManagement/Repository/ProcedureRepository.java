package com.example.HospitalManagement.Repository;
import com.example.HospitalManagement.Entity.Procedure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
@RepositoryRestResource(path = "procedures")
public interface ProcedureRepository extends JpaRepository<Procedure,Integer>{
   List<Procedure> findByNameIgnoreCase(String name);

}
