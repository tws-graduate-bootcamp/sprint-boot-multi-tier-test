package com.oocl.web.sampleWebApp.models;

import java.util.Objects;

public class CreateParkingBoyRequest {
    private String employeeId;
    private static final int MAX_EMPLOYEE_ID_LENGTH = 64;

    public String getEmployeeId() {
        return employeeId;
    }

    public static CreateParkingBoyRequest create(String employeeId) {
        final CreateParkingBoyRequest request = new CreateParkingBoyRequest();
        request.employeeId = employeeId;
        if (!request.isValid()) {
            throw new IllegalArgumentException("employeeId:" + employeeId);
        }
        return request;
    }

    public boolean isValid() {
        return employeeId != null && !employeeId.isEmpty() && employeeId.length() <= MAX_EMPLOYEE_ID_LENGTH;
    }
}
