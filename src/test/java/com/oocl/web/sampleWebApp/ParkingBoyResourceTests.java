package com.oocl.web.sampleWebApp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oocl.web.sampleWebApp.domain.ParkingBoy;
import com.oocl.web.sampleWebApp.domain.ParkingBoyRepository;
import com.oocl.web.sampleWebApp.domain.ParkingLot;
import com.oocl.web.sampleWebApp.models.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.oocl.web.sampleWebApp.WebTestUtil.getContentAsObject;
import static com.oocl.web.sampleWebApp.WebTestUtil.toJsonString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ParkingBoyResourceTests {
    @Autowired
    private ParkingBoyRepository parkingBoyRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mvc;

	@Test
	public void should_get_parking_boys() throws Exception {
	    // Given
        final ParkingBoy boy = parkingBoyRepository.save(new ParkingBoy("boy"));

        // When
        final MvcResult result = mvc.perform(get("/parkingboys"))
            .andReturn();

        // Then
        assertEquals(200, result.getResponse().getStatus());

        final ParkingBoyResponse[] parkingBoys = getContentAsObject(result, ParkingBoyResponse[].class);

        assertEquals(1, parkingBoys.length);
        assertEquals("boy", parkingBoys[0].getEmployeeId());
    }

    @Test
    public void should_get_empty_array_if_there_is_no_parking_boy() throws Exception {
        // When
        final MvcResult result = mvc.perform(get("/parkingboys"))
            .andReturn();

        // Then
        assertEquals(200, result.getResponse().getStatus());

        final ParkingBoyResponse[] parkingBoys = getContentAsObject(result, ParkingBoyResponse[].class);

        assertEquals(0, parkingBoys.length);
    }

    @Test
    public void should_create_parking_boy() throws Exception {
	    // Given
        CreateParkingBoyRequest request = CreateParkingBoyRequest.create("employee-01");

        // When
        mvc.perform(post("/parkingboys")
            .content(toJsonString(request)).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        // Then
        ParkingBoyResponse[] parkingBoys = getContentAsObject(
            mvc.perform(get("/parkingboys")).andReturn(), ParkingBoyResponse[].class);
        assertEquals(1, parkingBoys.length);
        assertEquals("employee-01", parkingBoys[0].getEmployeeId());
    }

    @Test
    public void should_get_400_if_parking_boy_request_is_not_provided() throws Exception {
        mvc.perform(post("/parkingboys"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void should_get_400_if_employee_id_is_not_provided() throws Exception {
        mvc.perform(post("/parkingboys")
            .content("{\"employeeId\": null}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void should_get_400_if_employee_id_is_empty_string() throws Exception {
        mvc.perform(post("/parkingboys")
            .content("{\"employeeId\": \"\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void should_get_400_if_employee_id_is_too_long() throws Exception {
	    final String longEmployeeId = "0123456789012345678901234567890123456789012345678901234567890123456789";

        mvc.perform(post("/parkingboys")
            .content(String.format("{\"employeeId\": \"%s\"}", longEmployeeId))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void should_get_400_if_employee_id_conflicts() throws Exception {
        final String duplicatedId = "employee-01";

        mvc.perform(post("/parkingboys")
            .content(toJsonString(CreateParkingBoyRequest.create(duplicatedId)))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        mvc.perform(post("/parkingboys")
            .content(toJsonString(CreateParkingBoyRequest.create(duplicatedId)))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void should_get_parking_boy_with_parking_lots() throws Exception {
	    // Given
        final ParkingBoy employee = new ParkingBoy("e01");
        final ParkingLot p01 = new ParkingLot("p01", 2);
        p01.setParkingBoy(employee);
        final ParkingLot p02 = new ParkingLot("p02", 3);
        p02.setParkingBoy(employee);
        entityManager.persist(p01);
        entityManager.persist(p02);
        entityManager.flush();

        // When
        final MvcResult result = mvc.perform(get("/parkingboys/e01"))
            .andExpect(status().isOk())
            .andReturn();

        final ParkingBoyWithParkingLotResponse response = getContentAsObject(
            result, ParkingBoyWithParkingLotResponse.class);
        assertEquals("e01", response.getEmployeeId());
        List<ParkingLotResponse> parkingLots = response.getParkingLots();
        assertEquals(2, parkingLots.size());
        assertTrue(parkingLots.stream().anyMatch(pl -> pl.getParkingLotId().equals("p01") && pl.getCapacity() == 2));
        assertTrue(parkingLots.stream().anyMatch(pl -> pl.getParkingLotId().equals("p02") && pl.getCapacity() == 3));
    }

    @Test
    public void should_get_404_when_parking_boy_not_exist_when_get_parking_boy_with_parking_lots() throws Exception {
        mvc.perform(get("/parkingboys/not-exist-id"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void should_associate_parking_boy_with_parking_lot() throws Exception {
	    // Given
        final ParkingBoy employee = new ParkingBoy("e01");
        final ParkingLot p01 = new ParkingLot("p01", 2);
        entityManager.persist(employee);
        entityManager.persist(p01);

        AssociateParkingBoyParkingLotRequest request = AssociateParkingBoyParkingLotRequest.create("p01");

        // When
        mvc.perform(post("/parkingboys/e01/parkinglots")
            .content(toJsonString(request)).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        // Then
        final ParkingBoyWithParkingLotResponse parkingBoyWithParkingLots = getContentAsObject(
            mvc.perform(get("/parkingboys/e01")).andReturn(), ParkingBoyWithParkingLotResponse.class);
        assertEquals("p01", parkingBoyWithParkingLots.getParkingLots().get(0).getParkingLotId());
    }

    @Test
    public void should_get_400_if_parking_lot_id_is_not_provided() throws Exception {
        // Given
        final ParkingBoy employee = new ParkingBoy("e01");
        final ParkingLot p01 = new ParkingLot("p01", 2);
        entityManager.persist(employee);
        entityManager.persist(p01);

        // When
        mvc.perform(post("/parkingboys/e01/parkinglots")
            .content("{}").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void should_get_400_if_request_is_not_provided() throws Exception {
        // Given
        final ParkingBoy employee = new ParkingBoy("e01");
        final ParkingLot p01 = new ParkingLot("p01", 2);
        entityManager.persist(employee);
        entityManager.persist(p01);

        // When
        mvc.perform(post("/parkingboys/e01/parkinglots"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void should_get_400_if_parking_boy_does_not_exist() throws Exception {
        // Given
        final ParkingBoy employee = new ParkingBoy("e01");
        final ParkingLot p01 = new ParkingLot("p01", 2);
        entityManager.persist(employee);
        entityManager.persist(p01);
        AssociateParkingBoyParkingLotRequest request = AssociateParkingBoyParkingLotRequest.create("p01");

        // When
        mvc.perform(post("/parkingboys/e02/parkinglots")
            .content(toJsonString(request)).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
}
