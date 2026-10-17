package de.denys.departureboard.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.denys.departureboard.util.TimeUtil;

@Component
@ConditionalOnProperty(name = "app.import.enabled", havingValue = "true")
public class GtfsImporter implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(GtfsImporter.class);
    private static final int BATCH_SIZE = 5000;

    private final JdbcTemplate jdbc;
    private final String path;

    public GtfsImporter(JdbcTemplate jdbc, @Value("${app.import.path}") String path) {
        this.jdbc = jdbc;
        this.path = path;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
        Integer existing = jdbc.queryForObject("select count(*) from stop", Integer.class);
        if (existing != null && existing > 0) {
            log.info("GTFS data already imported, skipping");
            return;
        }
        log.info("Importing GTFS data from {}", path);

        load("stops.txt", "insert into stop(id, name, lat, lon) values (?, ?, ?, ?)",
                r -> new Object[]{r.get("stop_id"), r.get("stop_name"),
                        toDouble(r.get("stop_lat")), toDouble(r.get("stop_lon"))});

        load("routes.txt", "insert into route(id, short_name, long_name, route_type) values (?, ?, ?, ?)",
                r -> new Object[]{r.get("route_id"), r.get("route_short_name"),
                        r.get("route_long_name"), toInt(r.get("route_type"))});

        load("trips.txt", "insert into trip(id, route_id, headsign) values (?, ?, ?)",
                r -> new Object[]{r.get("trip_id"), r.get("route_id"), r.get("trip_headsign")});

        load("stop_times.txt",
                "insert into stop_time(trip_id, stop_id, stop_sequence, departure_seconds) values (?, ?, ?, ?)",
                r -> {
                    String dep = r.get("departure_time");
                    if (dep == null || dep.isBlank()) {
                        return null; // строки без времени пропускаем
                    }
                    return new Object[]{r.get("trip_id"), r.get("stop_id"),
                            Integer.parseInt(r.get("stop_sequence").trim()),
                            TimeUtil.parseGtfs(dep)};
                });

        log.info("GTFS import finished");
    }

    private void load(String file, String sql, Function<CSVRecord, Object[]> mapper) throws IOException {
        Path p = Path.of(path, file);
        try (BufferedReader reader = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            reader.mark(1);
            if (reader.read() != 0xFEFF) { // убрать BOM, если он есть
                reader.reset();
            }
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader().setSkipHeaderRecord(true).setIgnoreSurroundingSpaces(true).build();
            List<Object[]> batch = new ArrayList<>(BATCH_SIZE);
            int total = 0;
            try (CSVParser parser = format.parse(reader)) {
                for (CSVRecord record : parser) {
                    Object[] row = mapper.apply(record);
                    if (row == null) {
                        continue;
                    }
                    batch.add(row);
                    total++;
                    if (batch.size() == BATCH_SIZE) {
                        jdbc.batchUpdate(sql, batch);
                        batch.clear();
                    }
                }
            }
            if (!batch.isEmpty()) {
                jdbc.batchUpdate(sql, batch);
            }
            log.info("{}: {} rows", file, total);
        }
    }

    private static Double toDouble(String s) {
        return (s == null || s.isBlank()) ? null : Double.valueOf(s.trim());
    }

    private static Integer toInt(String s) {
        return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim());
    }
}