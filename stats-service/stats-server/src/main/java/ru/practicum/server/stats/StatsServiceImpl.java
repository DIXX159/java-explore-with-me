package ru.practicum.server.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.server.stats.model.Hit;
import ru.practicum.server.stats.model.HitMapper;
import ru.practicum.server.stats.model.Stats;
import ru.practicum.dto.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;

    private final HitMapper hitMapper;

    @Override
    public void createHit(EndpointHit hitDto) {
        Hit hit = hitMapper.toEntity(hitDto);
        if (hit != null) {
            hitRepository.save(hit);
        } else throw new RuntimeException();
    }

    @Override
    public List<Stats> getStats(String start, String end, String[] uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (!unique) {
            if (uris != null) {
                return hitRepository.findAllByUriContainsAndTimestampBetween(uris, LocalDateTime.parse(start, formatter), LocalDateTime.parse(end, formatter));
            } else {
                return hitRepository.findAllByTimestampBetween(LocalDateTime.parse(start, formatter), LocalDateTime.parse(end, formatter));
            }
        } else {
            if (uris != null) {
                return hitRepository.findUniqueStatsByUriContainsAndTimestampBetween(uris, LocalDateTime.parse(start, formatter), LocalDateTime.parse(end, formatter));
            } else {
                return hitRepository.findUniqueStatsByTimestampBetween(LocalDateTime.parse(start, formatter), LocalDateTime.parse(end, formatter));
            }
        }
    }
}