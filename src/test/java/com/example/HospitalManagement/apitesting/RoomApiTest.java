package com.example.HospitalManagement.apitesting;

import com.example.HospitalManagement.Entity.Block;
import com.example.HospitalManagement.Entity.Room;
import com.example.HospitalManagement.Repository.BlockRepository;
import com.example.HospitalManagement.Repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BlockRepository blockRepository;

    private Block testBlock;

    @BeforeEach
    void setUp() {
        testBlock = new Block(1, 100);
        blockRepository.save(testBlock);

        Room room1 = new Room();
        room1.setRoomNumber(101);
        room1.setRoomType("ICU");
        room1.setUnavailable(false);
        room1.setBlock(testBlock);

        Room room2 = new Room();
        room2.setRoomNumber(102);
        room2.setRoomType("General");
        room2.setUnavailable(false);
        room2.setBlock(testBlock);

        Room room3 = new Room();
        room3.setRoomNumber(103);
        room3.setRoomType("ICU");
        room3.setUnavailable(true);
        room3.setBlock(testBlock);

        roomRepository.saveAll(List.of(room1, room2, room3));
    }

    @AfterEach
    void tearDown() {
        roomRepository.deleteAll();
        blockRepository.deleteAll();
    }

    @Test
    @DisplayName("API Test: findByUnavailable=false should return 200 with room data")
    void testFindByUnavailable_False_ReturnsOk() throws Exception {
        mockMvc.perform(get("/rooms/search/findByUnavailable")
                        .param("unavailable", "false")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms").isArray())
                .andExpect(jsonPath("$._embedded.rooms[*].unavailable").exists())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    @DisplayName("API Test: findByRoomType=ICU should return 200 with room data")
    void testFindByRoomType_ICU_ReturnsOk() throws Exception {
        mockMvc.perform(get("/rooms/search/findByRoomType")
                        .param("type", "ICU")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms").isArray())
                .andExpect(jsonPath("$._embedded.rooms[*].roomType").exists())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    @DisplayName("API Test: findByRoomType=NonExistent should return 200 with empty result")
    void testFindByRoomType_NonExistent_ReturnsEmpty() throws Exception {
        mockMvc.perform(get("/rooms/search/findByRoomType")
                        .param("type", "NonExistent")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }
    
}
