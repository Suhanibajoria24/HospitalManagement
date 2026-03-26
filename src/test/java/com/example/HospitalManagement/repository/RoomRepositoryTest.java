package com.example.HospitalManagement.repository;

import com.example.HospitalManagement.Entity.Block;
import com.example.HospitalManagement.Entity.Room;
import com.example.HospitalManagement.Repository.BlockRepository;
import com.example.HospitalManagement.Repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BlockRepository blockRepository;

    private List<Room> rooms;
    private List<Block> blocks;

    @BeforeEach
    public void setUp() {
        Block block1 = new Block(7,1);
        Block block2 = new Block(7,2);
        blocks = Arrays.asList(block1,block2);
        blockRepository.saveAll(blocks);
        Room room1 = new Room(701, "ICU",false, block1);
        Room room2 = new Room(702, "General",false,block1);
        Room room3 = new Room(703, "ICU",false, block2);
        Room room4 = new Room(704, "General",false,block2);
        rooms = Arrays.asList(room1,room2,room3,room4);
        roomRepository.saveAll(rooms);
    }

    @AfterEach
    public void flushData(){
        roomRepository.deleteAll(rooms);
        blockRepository.deleteAll(blocks);
    }

    @Test
    @DisplayName("Test #1: unavailable = false + data exists - should return non-empty list")
    public void testFindByUnavailableTrue_WithData_ReturnsNonEmptyList() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> result = roomRepository.findByUnavailable(false, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getContent()).extracting(Room::getUnavailable).containsOnly(false);
    }

    @Test
    @DisplayName("Test #2: unavailable = true + no data - should return empty list")
    public void testFindByUnavailableTrue_WithoutData_ReturnsEmptyList() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> result = roomRepository.findByUnavailable(true, pageable);

        assertThat(result).isEmpty();
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    @DisplayName("Test #3: type = ICU + data exists - should return non-empty list")
    public void testFindByTypeICU_WithData_ReturnsNonEmptyList() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> result = roomRepository.findByRoomType ("ICU", pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Room::getRoomType).containsOnly("ICU");
    }

    @Test
    @DisplayName("Test #4: type = Single + no data - should return empty list")
    public void testFindByTypeSingle_WithoutData_ReturnsEmptyList() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> result = roomRepository.findByRoomType("Single", pageable);

        assertThat(result).isEmpty();
        assertThat(result.getContent()).hasSize(0);
    }




}
