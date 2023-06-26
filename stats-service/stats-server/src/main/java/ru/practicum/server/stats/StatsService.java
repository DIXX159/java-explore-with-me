package ru.practicum.server.stats;

import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.stats.model.Stats;
import ru.practicum.dto.EndpointHit;

import java.util.List;

public interface StatsService {

    void createHit(EndpointHit hitDto);

    List<Stats> getStats(String start, String end, String[] uris, Boolean unique) throws ValidationException;
}