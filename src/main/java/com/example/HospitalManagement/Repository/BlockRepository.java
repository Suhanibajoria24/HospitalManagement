package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.Block;
import com.example.HospitalManagement.Entity.BlockId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, BlockId> {
}
