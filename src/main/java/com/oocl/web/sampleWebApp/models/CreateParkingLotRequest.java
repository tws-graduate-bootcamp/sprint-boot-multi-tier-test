package com.oocl.web.sampleWebApp.models;

public class CreateParkingLotRequest {
    private static final int MAX_PARKING_LOT_ID_LENGTH = 64;
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

        if (!request.isValid()) {
            throw new IllegalArgumentException();
        }
        return request;
    }

    public boolean isValid() {
        return parkingLotId != null && parkingLotId.length() > 0 &&
            parkingLotId.length() <= MAX_PARKING_LOT_ID_LENGTH && capacity > 0;
    }
}
