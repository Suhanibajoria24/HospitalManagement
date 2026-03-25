package com.example.HospitalManagement.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "Block")
@Data
@IdClass(BlockId.class)
@NoArgsConstructor
public class Block {

    @Id
    @Column(name = "BlockFloor")
    private Integer blockFloor;

    @Id
    @Column(name = "BlockCode")
    private Integer blockCode;

    @ToString.Exclude
    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL)
    private List<Room> rooms;

    public Block(Integer blockFloor, Integer blockCode) {
        this.blockFloor = blockFloor;
        this.blockCode = blockCode;
    }
}
