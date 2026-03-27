package com.example.HospitalManagement.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "Block")
@Getter
@Setter
@IdClass(BlockId.class)
@AllArgsConstructor
@NoArgsConstructor
public class Block {

    @Id
    @Column(name = "BlockFloor")
    private Integer blockFloor;

    @Id
    @Column(name = "BlockCode")
    private Integer blockCode;

    @JsonIgnore
    @OneToMany(mappedBy = "block", fetch = FetchType.LAZY)
    private List<Room> rooms;

    @JsonIgnore
    @OneToMany(mappedBy = "block", fetch = FetchType.LAZY)
    private List<OnCall> onCallSchedules;

    public Block(Integer blockFloor, Integer blockCode) {
        this.blockFloor = blockFloor;
        this.blockCode = blockCode;
    }
}
