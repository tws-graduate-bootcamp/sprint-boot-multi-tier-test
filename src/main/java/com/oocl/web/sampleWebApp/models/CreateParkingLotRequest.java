package com.oocl.web.sampleWebApp.models;

public class CreateParkingLotRequest {
    private String parkingLotId;
    private int capacity;

    public String getParkingLotId() {
        return parkingLotId;
    }

    public int getCapacity() {
        return capacity;
    }

    public static CreateParkingLotRequest create(String parkingLotId, int capacity) {
        final CreateParkingLotRequest request = new CreateParkingLotRequest();
        request.parkingLotId = parkingLotId;
        request.capacity = capacity;
        return request;
    }

    public boolean isValid() {
        return parkingLotId != null;
    }
}
