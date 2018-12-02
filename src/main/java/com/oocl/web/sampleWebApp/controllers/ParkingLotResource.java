package com.oocl.web.sampleWebApp.controllers;

import com.oocl.web.sampleWebApp.domain.ParkingLot;
import com.oocl.web.sampleWebApp.domain.ParkingLotRepository;
import com.oocl.web.sampleWebApp.models.CreateParkingLotRequest;
import com.oocl.web.sampleWebApp.models.ParkingLotResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity create(@RequestBody CreateParkingLotRequest request) {
        if (!request.isValid()) {
            return ResponseEntity.badRequest().build();
        }

        final ParkingLot parkingLot = new ParkingLot(request.getParkingLotId(), request.getCapacity());
        parkingLotRepository.saveAndFlush(parkingLot);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
