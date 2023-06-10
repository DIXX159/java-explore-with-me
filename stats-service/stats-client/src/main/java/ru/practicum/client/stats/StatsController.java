package ru.practicum.client.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHit;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatClient statClient;

    @PostMapping(value = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public String createHit(@RequestBody @Valid EndpointHit hitDto,
                           HttpServletRequest request) {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI(), hitDto);
        statClient.createHit(hitDto);
        return "Информация сохранена";
    }

    @GetMapping(value = "/stats")
    public ResponseEntity<Object> getStats(@RequestParam(name = "start") String start,
                                           @RequestParam(name = "end") String end,
                                           @RequestParam(name = "uris", defaultValue = "null") String[] uris,
                                           @RequestParam(name = "unique", defaultValue = "false") Boolean unique,
                                           HttpServletRequest request) {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return statClient.getStats(start, end, uris, unique);
    }
}