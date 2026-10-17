package de.denys.departureboard.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.denys.departureboard.dto.DepartureDto;
import de.denys.departureboard.dto.DeparturesResponse;
import de.denys.departureboard.dto.StopDto;
import de.denys.departureboard.exception.StopNotFoundException;
import de.denys.departureboard.model.Stop;
import de.denys.departureboard.repository.StopRepository;
import de.denys.departureboard.repository.StopTimeRepository;
import de.denys.departureboard.util.TimeUtil;

@Service
@Transactional(readOnly = true)
public class DepartureService {

    private final StopRepository stops;
    private final StopTimeRepository stopTimes;

    public DepartureService(StopRepository stops, StopTimeRepository stopTimes) {
        this.stops = stops;
        this.stopTimes = stopTimes;
    }

    public List<StopDto> searchStops(String name, int limit) {
        return stops.findByNameContainingIgnoreCaseOrderByName(name, PageRequest.of(0, limit))
                .stream()
                .map(s -> new StopDto(s.getId(), s.getName(), s.getLat(), s.getLon()))
                .toList();
    }

    public DeparturesResponse departures(String stopId, String from, int limit) {
        Stop stop = stops.findById(stopId).orElseThrow(() -> new StopNotFoundException(stopId));
        int fromSeconds = TimeUtil.parseHhMm(from);
        List<DepartureDto> list = stopTimes
                .findDepartures(stopId, fromSeconds, PageRequest.of(0, limit))
                .stream()
                .map(r -> new DepartureDto(r.line(), r.headsign(), TimeUtil.format(r.departureSeconds())))
                .toList();
        return new DeparturesResponse(stop.getId(), stop.getName(), list);
    }
}
