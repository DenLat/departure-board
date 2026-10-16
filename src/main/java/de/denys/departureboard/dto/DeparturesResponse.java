package de.denys.departureboard.dto;

import java.util.List;

public record DeparturesResponse(String stopId, String stopName, List<DepartureDto> departures) {
}
