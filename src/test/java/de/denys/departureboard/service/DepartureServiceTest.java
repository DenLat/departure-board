package de.denys.departureboard.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import de.denys.departureboard.dto.DepartureRow;
import de.denys.departureboard.dto.DeparturesResponse;
import de.denys.departureboard.exception.StopNotFoundException;
import de.denys.departureboard.model.Stop;
import de.denys.departureboard.repository.StopRepository;
import de.denys.departureboard.repository.StopTimeRepository;

@ExtendWith(MockitoExtension.class)
class DepartureServiceTest {

    @Mock
    StopRepository stops;

    @Mock
    StopTimeRepository stopTimes;

    @InjectMocks
    DepartureService service;

    @Test
    void unknownStopThrows() {
        when(stops.findById("X")).thenReturn(Optional.empty());

        assertThrows(StopNotFoundException.class, () -> service.departures("X", "08:00", 10));
    }

    @Test
    void mapsRowsToDtoWithFormattedTime() {
        Stop stop = mock(Stop.class);
        when(stop.getId()).thenReturn("S1");
        when(stop.getName()).thenReturn("Alexanderplatz");
        when(stops.findById("S1")).thenReturn(Optional.of(stop));
        when(stopTimes.findDepartures(eq("S1"), eq(30000), any()))
                .thenReturn(List.of(new DepartureRow("U2", "Ruhleben", 30600)));

        DeparturesResponse result = service.departures("S1", "08:20", 5);

        assertEquals("08:30:00", result.departures().get(0).time());
        assertEquals("Alexanderplatz", result.stopName());
    }
}