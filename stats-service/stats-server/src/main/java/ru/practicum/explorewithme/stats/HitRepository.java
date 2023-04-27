package ru.practicum.explorewithme.stats;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.stats.model.Hit;
import ru.practicum.explorewithme.stats.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Integer> {

    @Query("select new ru.practicum.explorewithme.stats.model.Stats(h.app, h.uri, count(h.ip)) from Hit as h " +
            "where h.uri in ?1 and " +
            "h.timestamp between ?2 and ?3 " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<Stats> findAllByUriContainsAndTimestampBetween(String[] uris, LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.explorewithme.stats.model.Stats(h.app, h.uri, count(h.ip)) from Hit as h " +
            "where " +
            "h.timestamp between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<Stats> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.explorewithme.stats.model.Stats(h.app, h.uri, count(distinct h.ip)) from Hit as h " +
            "where " +
            "h.timestamp between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<Stats> findUniqueStatsByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.explorewithme.stats.model.Stats(h.app, h.uri, count(distinct h.ip)) from Hit as h " +
            "where h.uri in ?1 and " +
            "h.timestamp between ?2 and ?3 " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<Stats> findUniqueStatsByUriContainsAndTimestampBetween(String[] uris, LocalDateTime start, LocalDateTime end);
}