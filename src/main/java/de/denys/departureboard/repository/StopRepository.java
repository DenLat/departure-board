package de.denys.departureboard.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import de.denys.departureboard.model.Stop;

public interface StopRepository extends JpaRepository<Stop, String> {

    List<Stop> findByNameContainingIgnoreCaseOrderByName(String name, Pageable pageable);
}