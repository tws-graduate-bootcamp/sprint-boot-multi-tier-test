package com.oocl.web.sampleWebApp.controllers;

import com.oocl.web.sampleWebApp.domain.ParkingLotRepository;
import com.oocl.web.sampleWebApp.models.ParkingLotResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/parkinglots")
public class ParkingLotResource {
    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @GetMapping
    public ResponseEntity<List<ParkingLotResponse>> get() {
        final List<ParkingLotResponse> allParkingLots = parkingLotRepository.findAll()
            .stream()
            .map(pl -> ParkingLotResponse.create(pl.getParkingLotId(), pl.getCapacity()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(allParkingLots);
    }
}
