package com.oocl.web.sampleWebApp.models;

public class ParkingLotResponse {
    private String parkingLotId;
    private int capacity;

    public String getParkingLotId() {
        return parkingLotId;
    }

    public int getCapacity() {
        return capacity;
    }

    public static ParkingLotResponse create(String parkingLotId, int capacity) {
        final ParkingLotResponse response = new ParkingLotResponse();
        response.parkingLotId = parkingLotId;
        response.capacity = capacity;
        return response;
    }
}
