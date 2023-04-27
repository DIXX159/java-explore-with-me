package ru.practicum.explorewithme.stats;

import ru.practicum.EndpointHit;
import ru.practicum.explorewithme.stats.model.Stats;

import java.util.List;

public interface StatsService {

    EndpointHit createHit(EndpointHit hitDto);

    List<Stats> getStats(String start, String end, String[] uris, Boolean unique);
}