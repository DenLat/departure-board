package de.denys.departureboard.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.denys.departureboard.exception.StopNotFoundException;
import de.denys.departureboard.service.DepartureService;

@WebMvcTest(StopController.class)
class StopControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    DepartureService service;

    @Test
    void tooShortNameReturns400() throws Exception {
        mvc.perform(get("/api/stops").param("name", "a"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void limitAboveMaximumReturns400() throws Exception {
        mvc.perform(get("/api/stops/S1/departures").param("limit", "100"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownStopReturns404() throws Exception {
        when(service.departures(eq("X"), any(), anyInt()))
                .thenThrow(new StopNotFoundException("X"));

        mvc.perform(get("/api/stops/X/departures"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Stop not found"));
    }
}