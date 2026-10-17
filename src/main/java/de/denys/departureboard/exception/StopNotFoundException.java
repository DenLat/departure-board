package de.denys.departureboard.exception;

public class StopNotFoundException extends RuntimeException {

    public StopNotFoundException(String stopId) {
        super("Stop not found: " + stopId);
    }
}
