package ru.practicum.explorewithme.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHit;
import ru.practicum.explorewithme.stats.model.Stats;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsService statsService;

    @PostMapping(value = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public EndpointHit createHit(@RequestBody EndpointHit hitDto, HttpServletRequest request) {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI(), hitDto);
        return statsService.createHit(hitDto);
    }

    @GetMapping(value = "/stats")
    @ResponseBody
    public List<Stats> getStats(@RequestParam(name = "start") String start,
                                @RequestParam(name = "end") String end,
                                @RequestParam(name = "uris", required = false) String[] uris,
                                @RequestParam(name = "unique", defaultValue = "false") Boolean unique,
                                HttpServletRequest request) {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI());
        return statsService.getStats(start, end, uris, unique);
    }
}