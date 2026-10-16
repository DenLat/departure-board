package de.denys.departureboard.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.denys.departureboard.dto.DepartureRow;
import de.denys.departureboard.model.StopTime;

public interface StopTimeRepository extends JpaRepository<StopTime, Long> {

    @Query("""
        select new de.denys.departureboard.dto.DepartureRow(
            r.shortName, t.headsign, st.departureSeconds)
        from StopTime st
        join st.trip t
        join t.route r
        where st.stop.id = :stopId
          and st.departureSeconds >= :fromSeconds
        order by st.departureSeconds
        """)
    List<DepartureRow> findDepartures(@Param("stopId") String stopId,
                                      @Param("fromSeconds") int fromSeconds,
                                      Pageable pageable);
}