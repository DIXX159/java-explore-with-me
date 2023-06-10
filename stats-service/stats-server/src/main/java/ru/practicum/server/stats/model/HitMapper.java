package ru.practicum.server.stats.model;

import org.springframework.stereotype.Component;
import ru.practicum.dto.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class HitMapper {

    public Hit toEntity(EndpointHit hitDto) {
        if (hitDto == null) {
            return null;
        }
        Hit hit = new Hit();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(LocalDateTime.parse(hitDto.getTimestamp(), formatter));
        return hit;
    }
}