package com.oocl.web.sampleWebApp;

import com.oocl.web.sampleWebApp.domain.ParkingLot;
import com.oocl.web.sampleWebApp.domain.ParkingLotRepository;
import com.oocl.web.sampleWebApp.models.CreateParkingLotRequest;
import com.oocl.web.sampleWebApp.models.ParkingLotResponse;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.oocl.web.sampleWebApp.WebTestUtil.getContentAsObject;
import static com.oocl.web.sampleWebApp.WebTestUtil.toJsonString;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ParkingLotResourceTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Test
    public void should_get_parking_boys() throws Exception {
        // Given
        final ParkingLot parkingLot = parkingLotRepository.save(new ParkingLot("p01", 10));

        // When
        final MvcResult result = mvc.perform(get("/parkinglots"))
            .andReturn();

        // Then
        assertEquals(200, result.getResponse().getStatus());

        final ParkingLotResponse[] parkingLots = getContentAsObject(result, ParkingLotResponse[].class);

        assertEquals(1, parkingLots.length);
        assertEquals("p01", parkingLots[0].getParkingLotId());
        assertEquals(10, parkingLots[0].getCapacity());
    }

    @Test
    public void should_get_empty_array_if_there_is_no_parking_lot() throws Exception {
        // When
        final MvcResult result = mvc.perform(get("/parkinglots"))
            .andReturn();

        // Then
        assertEquals(200, result.getResponse().getStatus());

        final ParkingLotResponse[] parkingLots = getContentAsObject(result, ParkingLotResponse[].class);

        assertEquals(0, parkingLots.length);
    }

    @Test
    public void should_be_able_to_create_parking_lot() throws Exception {
        // Given
        CreateParkingLotRequest request = CreateParkingLotRequest.create("p01", 10);

        // When
        mvc.perform(
            post("/parkinglots")
                .content(toJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        // Then
        final ParkingLotResponse[] allParkingLots = getContentAsObject(
            mvc.perform(get("/parkinglots")).andReturn(), ParkingLotResponse[].class);
        assertEquals(1, allParkingLots.length);
        assertEquals(10, allParkingLots[0].getCapacity());
        assertEquals("p01", allParkingLots[0].getParkingLotId());
    }
}
