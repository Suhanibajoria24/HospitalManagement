package com.example.HospitalManagement.repository;

import com.example.HospitalManagement.Entity.Room;
import com.example.HospitalManagement.Repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class RoomRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    public void setUp() {
        roomRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Test #1: unavailable = true + data exists - should return non-empty list")
    public void testFindByUnavailableTrue_WithData_ReturnsNonEmptyList() {
        Room room1 = new Room(101, "ICU", 1, 1, true);
        Room room2 = new Room(102, "General", 1, 2, true);
        Room room3 = new Room(103, "Private", 2, 1, false);
        
        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.persist(room3);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> result = roomRepository.findByUnavailable(true, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Room::getUnavailable).containsOnly(true);
    }

    @Test
    @DisplayName("Test #2: unavailable = true + no data - should return empty list")
    public void testFindByUnavailableTrue_WithoutData_ReturnsEmptyList() {
        Room room1 = new Room(101, "ICU", 1, 1, false);
        Room room2 = new Room(102, "General", 1, 2, false);
        
        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> result = roomRepository.findByUnavailable(true, pageable);

        assertThat(result).isEmpty();
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    @DisplayName("Test #3: unavailable = false + data exists - should return non-empty list")
    public void testFindByUnavailableFalse_WithData_ReturnsNonEmptyList() {
        Room room1 = new Room(101, "ICU", 1, 1, false);
        Room room2 = new Room(102, "General", 1, 2, false);
        Room room3 = new Room(103, "Private", 2, 1, true);
        
        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.persist(room3);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> result = roomRepository.findByUnavailable(false, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Room::getUnavailable).containsOnly(false);
    }

    @Test
    @DisplayName("Test #4: unavailable = false + no data - should return empty list")
    public void testFindByUnavailableFalse_WithoutData_ReturnsEmptyList() {
        Room room1 = new Room(101, "ICU", 1, 1, true);
        Room room2 = new Room(102, "General", 1, 2, true);
        
        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> result = roomRepository.findByUnavailable(false, pageable);

        assertThat(result).isEmpty();
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    @DisplayName("Test #5: Valid pagination - should return correct subset")
    public void testFindByUnavailable_WithValidPagination_ReturnsCorrectSubset() {
        for (int i = 1; i <= 10; i++) {
            Room room = new Room(100 + i, "Room" + i, i % 3 + 1, i % 4 + 1, false);
            entityManager.persist(room);
        }
        entityManager.flush();

        Pageable firstPage = PageRequest.of(0, 3);
        Page<Room> firstPageResult = roomRepository.findByUnavailable(false, firstPage);

        assertThat(firstPageResult.getContent()).hasSize(3);
        assertThat(firstPageResult.getTotalElements()).isEqualTo(10);
        assertThat(firstPageResult.getTotalPages()).isEqualTo(4);
        assertThat(firstPageResult.getNumber()).isEqualTo(0);

        Pageable secondPage = PageRequest.of(1, 3);
        Page<Room> secondPageResult = roomRepository.findByUnavailable(false, secondPage);

        assertThat(secondPageResult.getContent()).hasSize(3);
        assertThat(secondPageResult.getNumber()).isEqualTo(1);
        assertThat(secondPageResult.getContent()).isNotEqualTo(firstPageResult.getContent());
    }

    @Test
    @DisplayName("Test #6: Invalid pagination - should return empty result")
    public void testFindByUnavailable_WithInvalidPagination_ReturnsEmptyResult() {
        Room room1 = new Room(101, "ICU", 1, 1, false);
        Room room2 = new Room(102, "General", 1, 2, false);
        
        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.flush();

        Pageable invalidPage = PageRequest.of(10, 5);
        Page<Room> result = roomRepository.findByUnavailable(false, invalidPage);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getNumber()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test #7: unavailable = null - should search for IS NULL (returns rooms with null unavailable field)")
    public void testFindByUnavailable_WithNull_SearchesForNullValues() {
        Room room1 = new Room(101, "ICU", 1, 1, false);
        Room room2 = new Room(102, "General", 1, 2, true);
        Room room3 = new Room(103, "Private", 2, 1, null);
        Room room4 = new Room(104, "VIP", 2, 2, null);
        
        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.persist(room3);
        entityManager.persist(room4);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> result = roomRepository.findByUnavailable(null, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Room::getUnavailable).containsOnly((Boolean) null);
        assertThat(result.getContent()).extracting(Room::getRoomNumber).containsExactlyInAnyOrder(103, 104);
    }
}
