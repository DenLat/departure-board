package de.denys.departureboard.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stop")
public class Stop {

    @Id
    private String id;
    private String name;
    private Double lat;
    private Double lon;

    protected Stop() {
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Double getLat() { return lat; }
    public Double getLon() { return lon; }
}