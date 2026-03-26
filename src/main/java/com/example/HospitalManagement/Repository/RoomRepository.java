package com.example.HospitalManagement.Repository;


import com.example.HospitalManagement.Entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(path = "rooms")
public interface RoomRepository extends JpaRepository<Room, Integer> {

    Page<Room> findByUnavailable(Boolean unavailable, Pageable pageable);

    Page<Room> findByRoomType(String type,Pageable pageable);
}