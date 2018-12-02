package com.oocl.web.sampleWebApp.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    List<ParkingLot> findAllByParkingBoy(ParkingBoy parkingBoy);
    ParkingLot findOneByParkingLotId(String parkingLotId);
}
