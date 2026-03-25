package com.example.HospitalManagement.Entity;



    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Room")
public class Room {

    @Id
    @Column(name = "RoomNumber")
    private Integer roomNumber;

    @Column(name = "RoomType", nullable = false)
    private String roomType;

    @Column(name = "Unavailable", nullable = false)
    private Boolean unavailable;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "BlockFloor", referencedColumnName = "BlockFloor"),
            @JoinColumn(name = "BlockCode",  referencedColumnName = "BlockCode")
    })
    private Block block;

}
