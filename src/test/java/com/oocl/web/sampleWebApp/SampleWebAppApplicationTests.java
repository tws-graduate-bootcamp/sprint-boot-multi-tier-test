package com.oocl.web.sampleWebApp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oocl.web.sampleWebApp.domain.ParkingBoy;
import com.oocl.web.sampleWebApp.domain.ParkingBoyRepository;
import com.oocl.web.sampleWebApp.models.CreateParkingBoyRequest;
import com.oocl.web.sampleWebApp.models.ParkingBoyResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.oocl.web.sampleWebApp.WebTestUtil.getContentAsObject;
import static com.oocl.web.sampleWebApp.WebTestUtil.toJsonString;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SampleWebAppApplicationTests {
    @Autowired
    private ParkingBoyRepository parkingBoyRepository;

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
}
