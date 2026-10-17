package de.denys.departureboard.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import de.denys.departureboard.exception.InvalidRequestException;

class TimeUtilTest {

    @Test
    void parsesGtfsTimeBeyond24Hours() {
        assertEquals(90600, TimeUtil.parseGtfs("25:10:00"));
    }

    @Test
    void formatsSecondsBackToGtfs() {
        assertEquals("25:10:00", TimeUtil.format(90600));
    }

    @Test
    void parsesHhMm() {
        assertEquals(30600, TimeUtil.parseHhMm("08:30"));
    }

    @Test
    void rejectsBadFormat() {
        assertThrows(InvalidRequestException.class, () -> TimeUtil.parseHhMm("abc"));
    }
}