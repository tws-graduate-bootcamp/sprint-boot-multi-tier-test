package com.oocl.web.sampleWebApp.models;

import java.util.Objects;

public class ParkingBoyResponse {
    private String employeeId;

    public String getEmployeeId() {
        return employeeId;
    }

    public static ParkingBoyResponse create(String employeeId) {
        Objects.requireNonNull(employeeId);

        final ParkingBoyResponse response = new ParkingBoyResponse();
        response.employeeId = employeeId;
        return response;
    }
}
