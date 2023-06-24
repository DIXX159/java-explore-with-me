package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHit;
import ru.practicum.ewm.client.StatsServiceClient;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.service.PublicService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPublicController {

    private final PublicService publicService;
    private final StatsServiceClient statsServiceClient;

    @GetMapping(value = "/events")
    public List<EventShortDto> getFilteredEvents(@RequestParam(name = "text", required = false) String text,
                                                 @RequestParam(name = "categories", required = false) List<Long> categories,
                                                 @RequestParam(name = "paid", defaultValue = "false") Boolean paid,
                                                 @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                 @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                 @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                                 @RequestParam(name = "sort", required = false) String sort,
                                                 @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                 HttpServletRequest request) throws ValidationException {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        List<EventShortDto> events = publicService.getFilteredEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, PageRequest.of(from, size));
        for (EventShortDto eventShortDto : events) {
            statsServiceClient.createHit(new EndpointHit(null, "EWM Main", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }
        return events;
    }

    @GetMapping(value = "/events/{id}")
    public EventFullDto getEventById(@PathVariable Long id,
                                     HttpServletRequest request) throws ValidationException {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        EventFullDto event = publicService.getEventById(id);
        statsServiceClient.createHit(new EndpointHit(null, "EWM Main", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        return event;
    }
}