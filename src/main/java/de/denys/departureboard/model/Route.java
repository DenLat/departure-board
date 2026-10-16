package de.denys.departureboard.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "route")
public class Route {

    @Id
    private String id;
    private String shortName;
    private String longName;
    private Integer routeType;

    protected Route() {
    }

    public String getId() { return id; }
    public String getShortName() { return shortName; }
}
