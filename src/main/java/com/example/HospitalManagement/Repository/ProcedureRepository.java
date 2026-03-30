package com.example.HospitalManagement.Repository;
import com.example.HospitalManagement.Entity.Procedure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
@RepositoryRestResource(path = "procedures")
@Validated
public interface ProcedureRepository extends JpaRepository<Procedure,Integer>{
   List<Procedure> findByNameIgnoreCase(String name);

   @Query("SELECT p FROM Procedure p WHERE TRIM(p.name) = ''")
    List<Procedure> findProceduresWithBlankName();

}
