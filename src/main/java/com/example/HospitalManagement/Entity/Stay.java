package com.example.HospitalManagement.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Stay")
public class Stay {

    @Id
    @Column(name = "StayID")
    private Integer stayId;

    @NotNull
    @Column(name = "Patient", nullable = false)
    private Integer patient;

    @NotNull
    @Column(name = "Room", nullable = false)
    private Integer room;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "StayStart", nullable = false)
    private Date stayStart;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "StayEnd", nullable = false)
    private Date stayEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Patient", referencedColumnName = "SSN", insertable = false, updatable = false)
    private Patient patientEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Room", referencedColumnName = "RoomNumber", insertable = false, updatable = false)
    private Room roomEntity;

    @JsonIgnore
    @OneToMany(mappedBy = "stay", fetch = FetchType.LAZY)
    private List<Undergoes> undergoes;
}
