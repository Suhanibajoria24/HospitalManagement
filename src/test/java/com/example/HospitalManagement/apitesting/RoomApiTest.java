package com.example.HospitalManagement.apitesting;

import com.example.HospitalManagement.Entity.Block;
import com.example.HospitalManagement.Entity.BlockId;
import com.example.HospitalManagement.Entity.Room;
import com.example.HospitalManagement.Repository.BlockRepository;
import com.example.HospitalManagement.Repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomApiTest {

        private static final MediaType MERGE_PATCH_JSON = MediaType.parseMediaType("application/merge-patch+json");

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private RoomRepository roomRepository;

        @Autowired
        private BlockRepository blockRepository;

        private Block testBlock;
        private int testFloor;
        private int testCode;
        private int roomId1;
        private int roomId2;
        private int roomId3;
        private final Set<Integer> roomsToCleanup = new HashSet<>();
        private final Set<BlockId> blocksToCleanup = new HashSet<>();

        private BlockId nextUniqueBlockId() {
                int attempts = 0;
                while (attempts++ < 200) {
                        int floor = ThreadLocalRandom.current().nextInt(1000, 10000);
                        int code = ThreadLocalRandom.current().nextInt(1000, 10000);
                        BlockId blockId = new BlockId(floor, code);
                        if (!blockRepository.existsById(blockId)) {
                                return blockId;
                        }
                }
                throw new IllegalStateException("Could not generate unique BlockId for test");
        }

        private int nextUniqueRoomNumber() {
                int attempts = 0;
                while (attempts++ < 200) {
                        int roomNumber = ThreadLocalRandom.current().nextInt(100000, 900000);
                        if (!roomRepository.existsById(roomNumber)) {
                                return roomNumber;
                        }
                }
                throw new IllegalStateException("Could not generate unique roomNumber for test");
        }

        @BeforeEach
        void setUp() {
                roomsToCleanup.clear();
                blocksToCleanup.clear();

                BlockId blockId = nextUniqueBlockId();
                testFloor = blockId.getBlockFloor();
                testCode = blockId.getBlockCode();

                testBlock = new Block(testFloor, testCode);
                blockRepository.save(testBlock);
                blocksToCleanup.add(blockId);

                roomId1 = nextUniqueRoomNumber();
                roomId2 = nextUniqueRoomNumber();
                roomId3 = nextUniqueRoomNumber();

                Room room1 = new Room(roomId1, "ICU", false, testBlock);
                Room room2 = new Room(roomId2, "General", false, testBlock);
                Room room3 = new Room(roomId3, "ICU", true, testBlock);

                roomRepository.saveAll(List.of(room1, room2, room3));
                roomsToCleanup.add(roomId1);
                roomsToCleanup.add(roomId2);
                roomsToCleanup.add(roomId3);
        }

        @AfterEach
        void tearDown() {
                for (Integer roomId : roomsToCleanup) {
                        if (roomRepository.existsById(roomId)) {
                                roomRepository.deleteById(roomId);
                        }
                }
                roomsToCleanup.clear();

                for (BlockId blockId : blocksToCleanup) {
                        if (blockRepository.existsById(blockId)) {
                                blockRepository.deleteById(blockId);
                        }
                }
                blocksToCleanup.clear();
        }

        @Test
        @DisplayName("API Test1: findByUnavailable=false should return 200 with room data")
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
        @DisplayName("API Test2: findByRoomType=ICU should return 200 with room data")
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
        @DisplayName("API Test3: findByRoomType=NonExistent should return 200 with empty result")
        void testFindByRoomType_NonExistent_ReturnsEmpty() throws Exception {
                mockMvc.perform(get("/rooms/search/findByRoomType")
                                .param("type", "NonExistent")
                                .param("page", "0")
                                .param("size", "5"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.page").exists())
                                .andExpect(jsonPath("$.page.totalElements").value(0));
        }

        @Test
        @DisplayName("API Test 4: Create Room With Correct Data and it should get Created")
        void createRoom_WithValidData_ReturnsCreated() throws Exception {
                int roomNumber = nextUniqueRoomNumber();
                String body = String.format(
                                "{\"roomNumber\":%d,\"roomType\":\"Observation\",\"blockFloor\":%d,\"blockCode\":%d,\"unavailable\":false}",
                                roomNumber, testFloor, testCode);

                mockMvc.perform(post("/rooms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isCreated());

                roomsToCleanup.add(roomNumber);
                assertThat(roomRepository.existsById(roomNumber)).isTrue();
        }

        @Test
        @DisplayName("API Test 5: Create Room With No Existing Block (blockFloor and blockCode) and it should create a new Block and return 201 Created")
        void createRoom_WithNonExistentBlock_AutoCreatesBlockAndReturnsCreated() throws Exception {
                int roomNumber = nextUniqueRoomNumber();
                BlockId newBlockId = nextUniqueBlockId();
                String body = String.format(
                                "{\"roomNumber\":%d,\"roomType\":\"ICU\",\"blockFloor\":%d,\"blockCode\":%d,\"unavailable\":false}",
                                roomNumber, newBlockId.getBlockFloor(), newBlockId.getBlockCode());

                mockMvc.perform(post("/rooms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isCreated());
                roomsToCleanup.add(roomNumber);
                blocksToCleanup.add(newBlockId);
                assertThat(blockRepository.existsById(newBlockId)).isTrue();
                assertThat(roomRepository.existsById(roomNumber)).isTrue();
        }

        @Test
        @DisplayName("API Test 6: Create Room With No blockFloor and blockCode in JSON and it should return 400 Bad Request")
        void createRoom_WithoutBlockFloorAndBlockCode_ReturnsBadRequest() throws Exception {
                String body = """
                                {
                                  "roomNumber": 301,
                                  "roomType": "ICU",
                                  "unavailable": false
                                }
                                """;

                mockMvc.perform(post("/rooms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("API Test 7: Create Room With No body and it should return 400 Bad Request")
        void createRoom_WithEmptyBody_ReturnsBadRequest() throws Exception {
                mockMvc.perform(post("/rooms")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("API Test 8: Create Room with Invalid Data and it should return 400 Bad Request")
        void createRoom_WithMalformedJson_ReturnsBadRequest() throws Exception {
                mockMvc.perform(post("/rooms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{not-json"))
                                .andExpect(status().isBadRequest());
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("invalidRoomCreateBodies")
        @DisplayName("Create Room: invalid field combinations return 400 Bad Request")
        void createRoom_InvalidInputCombinations_ReturnsExpectedStatus(String description, String jsonBody,
                        int expectedHttpStatus) throws Exception {
                mockMvc.perform(post("/rooms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody))
                                .andExpect(status().is(expectedHttpStatus));
        }

        private static Stream<Arguments> invalidRoomCreateBodies() {
                String roomType31Chars = "A".repeat(31);
                int badRequest = HttpStatus.BAD_REQUEST.value();
                return Stream.of(
                                Arguments.of("blank roomType",
                                                "{\"roomNumber\":411,\"roomType\":\"\",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                                                badRequest),
                                Arguments.of("whitespace-only roomType",
                                                "{\"roomNumber\":412,\"roomType\":\"   \",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                                                badRequest),
                                Arguments.of("roomType longer than 30 characters",
                                                "{\"roomNumber\":413,\"roomType\":\"" + roomType31Chars
                                                                + "\",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                                                badRequest),
                                Arguments.of("null roomType",
                                                "{\"roomNumber\":414,\"roomType\":null,\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                                                badRequest),
                                Arguments.of("null unavailable",
                                                "{\"roomNumber\":415,\"roomType\":\"ICU\",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":null}",
                                                badRequest),
                                Arguments.of("null blockFloor",
                                                "{\"roomNumber\":416,\"roomType\":\"ICU\",\"blockFloor\":null,\"blockCode\":100,\"unavailable\":false}",
                                                badRequest),
                                Arguments.of("null blockCode",
                                                "{\"roomNumber\":417,\"roomType\":\"ICU\",\"blockFloor\":1,\"blockCode\":null,\"unavailable\":false}",
                                                badRequest),
                                Arguments.of("null roomNumber (Hibernate requires assigned id before persist)",
                                                "{\"roomNumber\":null,\"roomType\":\"ICU\",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                                                badRequest),
                                Arguments.of("roomNumber has wrong JSON type",
                                                "{\"roomNumber\":\"not-a-number\",\"roomType\":\"ICU\",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                                                badRequest),
                                Arguments.of("missing roomNumber property (Hibernate requires assigned id before persist)",
                                                "{\"roomType\":\"ICU\",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                                                badRequest),
                                Arguments.of("missing roomType property",
                                                "{\"roomNumber\":418,\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                                                badRequest),
                                Arguments.of("missing unavailable property",
                                                "{\"roomNumber\":419,\"roomType\":\"ICU\",\"blockFloor\":1,\"blockCode\":100}",
                                                badRequest));
        }

        @Test
        @DisplayName("Update PATCH: partial change roomType only returns 2xx and persists")
        void patchRoom_UpdateRoomTypeOnly_ReturnsSuccess() throws Exception {
                mockMvc.perform(patch("/rooms/" + roomId1)
                                .contentType(MERGE_PATCH_JSON)
                                .content("{\"roomType\":\"Deluxe\"}"))
                                .andExpect(status().is2xxSuccessful());

                Room updated = roomRepository.findById(roomId1).orElseThrow();
                assertThat(updated.getRoomType()).isEqualTo("Deluxe");
                assertThat(updated.getUnavailable()).isFalse();
                assertThat(updated.getBlockFloor()).isEqualTo(testFloor);
                assertThat(updated.getBlockCode()).isEqualTo(testCode);
        }

        @Test
        @DisplayName("Update PATCH: partial change unavailable only returns 2xx and persists")
        void patchRoom_UpdateUnavailableOnly_ReturnsSuccess() throws Exception {
                mockMvc.perform(patch("/rooms/" + roomId2)
                                .contentType(MERGE_PATCH_JSON)
                                .content("{\"unavailable\":true}"))
                                .andExpect(status().is2xxSuccessful());

                Room updated = roomRepository.findById(roomId2).orElseThrow();
                assertThat(updated.getUnavailable()).isTrue();
                assertThat(updated.getRoomType()).isEqualTo("General");
        }

        @Test
        @DisplayName("Update PATCH: change roomType and unavailable together returns 2xx and persists")
        void patchRoom_UpdateRoomTypeAndUnavailable_ReturnsSuccess() throws Exception {
                mockMvc.perform(patch("/rooms/" + roomId3)
                                .contentType(MERGE_PATCH_JSON)
                                .content("{\"roomType\":\"Isolation\",\"unavailable\":false}"))
                                .andExpect(status().is2xxSuccessful());

                Room updated = roomRepository.findById(roomId3).orElseThrow();
                assertThat(updated.getRoomType()).isEqualTo("Isolation");
                assertThat(updated.getUnavailable()).isFalse();
        }

        @Test
        @DisplayName("Update PATCH: empty merge-patch body returns 2xx (no-op)")
        void patchRoom_EmptyMergePatchBody_ReturnsSuccess() throws Exception {
                Room before = roomRepository.findById(roomId1).orElseThrow();
                mockMvc.perform(patch("/rooms/" + roomId1)
                                .contentType(MERGE_PATCH_JSON)
                                .content("{}"))
                                .andExpect(status().is2xxSuccessful());

                Room after = roomRepository.findById(roomId1).orElseThrow();
                assertThat(after.getRoomType()).isEqualTo(before.getRoomType());
                assertThat(after.getUnavailable()).isEqualTo(before.getUnavailable());
        }

        @Test
        @DisplayName("Update PATCH: non-existent room returns 404 Not Found")
        void patchRoom_NonExistentRoom_ReturnsNotFound() throws Exception {
                mockMvc.perform(patch("/rooms/999999")
                                .contentType(MERGE_PATCH_JSON)
                                .content("{\"roomType\":\"X\"}"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Update PATCH: malformed JSON returns 400 Bad Request")
        void patchRoom_MalformedJson_ReturnsBadRequest() throws Exception {
                mockMvc.perform(patch("/rooms/" + roomId1)
                                .contentType(MERGE_PATCH_JSON)
                                .content("{broken"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Update PUT: full replace with valid body returns 2xx and persists")
        void putRoom_FullReplace_ReturnsSuccess() throws Exception {
                String body = String.format(
                                "{\"roomNumber\":%d,\"roomType\":\"Surgery\",\"blockFloor\":%d,\"blockCode\":%d,\"unavailable\":true}",
                                roomId1, testFloor, testCode);

                mockMvc.perform(put("/rooms/" + roomId1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().is2xxSuccessful());

                Room updated = roomRepository.findById(roomId1).orElseThrow();
                assertThat(updated.getRoomType()).isEqualTo("Surgery");
                assertThat(updated.getUnavailable()).isTrue();
                assertThat(updated.getBlockFloor()).isEqualTo(testFloor);
                assertThat(updated.getBlockCode()).isEqualTo(testCode);
        }

        @Test
        @DisplayName("Update PUT: non-existent id upserts (Spring Data REST creates resource) returns 2xx")
        void putRoom_NonExistentId_UpsertsAndReturnsSuccess() throws Exception {
                int upsertRoomId = nextUniqueRoomNumber();
                String body = String.format(
                                "{\"roomNumber\":%d,\"roomType\":\"ICU\",\"blockFloor\":%d,\"blockCode\":%d,\"unavailable\":false}",
                                upsertRoomId, testFloor, testCode);

                mockMvc.perform(put("/rooms/" + upsertRoomId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().is2xxSuccessful());

                roomsToCleanup.add(upsertRoomId);
                assertThat(roomRepository.existsById(upsertRoomId)).isTrue();
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("invalidRoomPatchBodies")
        @DisplayName("Update PATCH: invalid merge-patch payloads return 400 Bad Request")
        void patchRoom_InvalidMergePatch_ReturnsBadRequest(String description, String jsonBody) throws Exception {
                mockMvc.perform(patch("/rooms/" + roomId1)
                                .contentType(MERGE_PATCH_JSON)
                                .content(jsonBody))
                                .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> invalidRoomPatchBodies() {
                String longType = "B".repeat(31);
                return Stream.of(
                                Arguments.of("blank roomType", "{\"roomType\":\"\"}"),
                                Arguments.of("whitespace-only roomType", "{\"roomType\":\"   \"}"),
                                Arguments.of("roomType longer than 30 characters",
                                                "{\"roomType\":\"" + longType + "\"}"),
                                Arguments.of("null roomType", "{\"roomType\":null}"),
                                Arguments.of("null unavailable", "{\"unavailable\":null}"),
                                Arguments.of("wrong type for unavailable", "{\"unavailable\":\"yes\"}"));
        }
}
