package com.oocl.web.sampleWebApp.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;

@Entity
@Table(name = "parking_lot")
public class ParkingLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parking_lot_id", length = 64, unique = true, nullable = false)
    private String parkingLotId;

    private Integer capacity;

    public Long getId() {
        return id;
    }

    public String getParkingLotId() {
        return parkingLotId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    protected ParkingLot() {}

    public ParkingLot(String parkingLotId, Integer capacity) {
        this.parkingLotId = parkingLotId;
        this.capacity = capacity;
    }
}

