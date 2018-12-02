package com.oocl.web.sampleWebApp.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ResponseUtils {
    public static ResponseEntity createStatusCodeResponse(HttpStatus status) {
        return ResponseEntity.status(status).build();
    }
}
