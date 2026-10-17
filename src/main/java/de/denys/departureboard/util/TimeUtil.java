package de.denys.departureboard.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import de.denys.departureboard.exception.InvalidRequestException;

public final class TimeUtil {

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    private TimeUtil() {
    }

    /** "08:30" -> секунды от полуночи, для параметра запроса */
    public static int parseHhMm(String value) {
        try {
            return LocalTime.parse(value, HH_MM).toSecondOfDay();
        } catch (DateTimeParseException e) {
            throw new InvalidRequestException("Parameter 'from' must be in HH:mm format");
        }
    }

    /** "25:10:00" -> секунды; в GTFS часы могут быть 24 и больше */
    public static int parseGtfs(String value) {
        String[] p = value.trim().split(":");
        return Integer.parseInt(p[0]) * 3600 + Integer.parseInt(p[1]) * 60 + Integer.parseInt(p[2]);
    }

    public static String format(int seconds) {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }
}