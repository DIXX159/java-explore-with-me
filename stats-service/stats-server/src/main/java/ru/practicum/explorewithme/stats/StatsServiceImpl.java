package ru.practicum.explorewithme.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.explorewithme.stats.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;

    private final HitMapper hitMapper;

    @Override
    public EndpointHit createHit(EndpointHit hitDto) {
        Hit hit = hitMapper.toEntity(hitDto);
        if (hit != null) {
            hitRepository.save(hit);
        }
        throw new RuntimeException();
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