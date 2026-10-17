package de.denys.departureboard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.denys.departureboard.dto.DeparturesResponse;
import de.denys.departureboard.dto.StopDto;
import de.denys.departureboard.service.DepartureService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/stops")
public class StopController {

    private final DepartureService service;

    public StopController(DepartureService service) {
        this.service = service;
    }

    @GetMapping
    public List<StopDto> search(
            @RequestParam @Size(min = 2, max = 100) String name,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int limit) {
        return service.searchStops(name, limit);
    }

    @GetMapping("/{stopId}/departures")
    public DeparturesResponse departures(
            @PathVariable String stopId,
            @RequestParam(defaultValue = "00:00") String from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        return service.departures(stopId, from, limit);
    }
}