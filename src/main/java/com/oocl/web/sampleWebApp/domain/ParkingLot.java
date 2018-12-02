package com.oocl.web.sampleWebApp.domain;

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

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "parking_boy_id")
    private ParkingBoy parkingBoy;

    public Long getId() {
        return id;
    }

    public String getParkingLotId() {
        return parkingLotId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public ParkingBoy getParkingBoy() {
        return parkingBoy;
    }

    public void setParkingBoy(ParkingBoy parkingBoy) {
        this.parkingBoy = parkingBoy;
    }

    protected ParkingLot() {}

    public ParkingLot(String parkingLotId, Integer capacity) {
        this.parkingLotId = parkingLotId;
        this.capacity = capacity;
    }
}

