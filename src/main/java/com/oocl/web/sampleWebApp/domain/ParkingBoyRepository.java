package com.oocl.web.sampleWebApp.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingBoyRepository extends JpaRepository<ParkingBoy, Long> {
    ParkingBoy findOneByEmployeeId(String employeeId);
}
