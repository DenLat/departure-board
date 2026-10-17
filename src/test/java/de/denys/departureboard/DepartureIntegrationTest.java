package de.denys.departureboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class DepartureIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    MockMvc mvc;

    @Test
    void returnsDeparturesAfterGivenTime() throws Exception {
        mvc.perform(get("/api/stops/S1/departures").param("from", "08:35").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stopName").value("Alexanderplatz"))
                .andExpect(jsonPath("$.departures[0].line").value("U2"))
                .andExpect(jsonPath("$.departures[0].time").value("08:40:00"));
    }

    @Test
    void findsStopByPartOfName() throws Exception {
        mvc.perform(get("/api/stops").param("name", "alex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("S1"));
    }
}