package com.oocl.web.sampleWebApp.controllers;

import com.oocl.web.sampleWebApp.domain.ParkingBoy;
import com.oocl.web.sampleWebApp.domain.ParkingBoyRepository;
import com.oocl.web.sampleWebApp.domain.ParkingLot;
import com.oocl.web.sampleWebApp.domain.ParkingLotRepository;
import com.oocl.web.sampleWebApp.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/parkingboys")
public class ParkingBoyResource {

    @Autowired
    private ParkingBoyRepository parkingBoyRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @GetMapping
    public ResponseEntity<ParkingBoyResponse[]> getAll() {
        final ParkingBoyResponse[] parkingBoys = parkingBoyRepository.findAll().stream()
            .map((ParkingBoy employeeId) -> ParkingBoyResponse.create(employeeId.getEmployeeId()))
            .toArray(ParkingBoyResponse[]::new);

        return ResponseEntity.ok(parkingBoys);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody CreateParkingBoyRequest request) {
        if (!request.isValid()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            parkingBoyRepository.saveAndFlush(new ParkingBoy(request.getEmployeeId()));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DataIntegrityViolationException error) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<ParkingBoyWithParkingLotResponse> get(@PathVariable String employeeId) {
        ParkingBoy parkingBoy = parkingBoyRepository.findOneByEmployeeId(employeeId);
        if (parkingBoy == null) {
            return ResponseEntity.notFound().build();
        }
        List<ParkingLot> parkingLots = parkingLotRepository.findAllByParkingBoy(parkingBoy);
        final ParkingBoyWithParkingLotResponse response = ParkingBoyWithParkingLotResponse.create(
            parkingBoy.getEmployeeId(),
            parkingLots.stream().map(pl ->
                ParkingLotResponse.create(pl.getParkingLotId(), pl.getCapacity())).collect(Collectors.toList())
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{employeeId}/parkinglots")
    public ResponseEntity associateParkingBoyWithParkingLot(
        @PathVariable String employeeId,
        @RequestBody AssociateParkingBoyParkingLotRequest request) {
        if (!request.isValid()) {
            return ResponseEntity.badRequest().build();
        }
        final ParkingBoy parkingBoy = parkingBoyRepository.findOneByEmployeeId(employeeId);
        final ParkingLot parkingLot = parkingLotRepository.findOneByParkingLotId(request.getParkingLotId());
        parkingLot.setParkingBoy(parkingBoy);
        parkingLotRepository.saveAndFlush(parkingLot);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

