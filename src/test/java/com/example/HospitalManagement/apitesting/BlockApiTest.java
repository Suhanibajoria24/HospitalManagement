package com.example.HospitalManagement.apitesting;

import com.example.HospitalManagement.Entity.BlockId;
import com.example.HospitalManagement.Repository.BlockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BlockApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BlockRepository blockRepository;

    private final List<BlockId> blocksToCleanup = new ArrayList<>();

    @AfterEach
    void tearDown() {
        for (BlockId blockId : blocksToCleanup) {
            if (blockRepository.existsById(blockId)) {
                blockRepository.deleteById(blockId);
            }
        }
        blocksToCleanup.clear();
    }

    @BeforeEach
    void setUp() {
        blocksToCleanup.clear();
    }

    private BlockId nextUniqueBlockId() {
        int attempts = 0;
        while (attempts++ < 100) {
            int floor = ThreadLocalRandom.current().nextInt(1000, 10000);
            int code = ThreadLocalRandom.current().nextInt(1000, 10000);
            BlockId id = new BlockId(floor, code);
            if (!blockRepository.existsById(id)) {
                return id;
            }
        }
        throw new IllegalStateException("Could not generate unique BlockId for test");
    }

    @Test
    @DisplayName("Test 1: Create a Block and It should Exists")
    void createBlock_AndItShouldExists() throws Exception {
        BlockId blockId = nextUniqueBlockId();
        String blockJson = String.format("{\"blockFloor\":%d,\"blockCode\":%d}", blockId.getBlockFloor(), blockId.getBlockCode());

        mockMvc.perform(post("/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockJson))
                .andExpect(status().isCreated());

        blocksToCleanup.add(blockId);
        assertThat(blockRepository.existsById(blockId)).isTrue();
    }

    @Test
    @DisplayName("Test 2: Create a Duplicate Block It Shoud Return 409 Confict error")
    void createDuplicateBlockAndItShouldReturnConfict() throws Exception {
        BlockId blockId = nextUniqueBlockId();
        String blockJson = String.format("{\"blockFloor\":%d,\"blockCode\":%d}", blockId.getBlockFloor(), blockId.getBlockCode());

        mockMvc.perform(post("/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockJson))
                .andExpect(status().isCreated());
        blocksToCleanup.add(blockId);

        mockMvc.perform(post("/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockJson))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Test 3: Create With Improper and Malfunctioned Request Body and it should return 400 Bad Request")
    void createWithInvalidReq_AndItShouldReturnConfict() throws Exception {
        mockMvc.perform(post("/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test 4: Create With No Body and it should return 400 Bad Request")
    void createWithNoBody_AndItShouldReturnConfict() throws Exception {
        mockMvc.perform(post("/blocks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
