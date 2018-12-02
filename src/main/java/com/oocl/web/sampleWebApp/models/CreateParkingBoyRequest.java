package com.oocl.web.sampleWebApp.models;

import java.util.Objects;

public class CreateParkingBoyRequest {
    private String employeeId;

    public String getEmployeeId() {
        return employeeId;
    }

    public static CreateParkingBoyRequest create(String employeeId) {
        Objects.requireNonNull(employeeId);

        final CreateParkingBoyRequest request = new CreateParkingBoyRequest();
        request.employeeId = employeeId;
        return request;
    }

    public boolean isValid() {
        return employeeId != null;
    }
}
