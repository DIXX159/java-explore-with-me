package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.ParticipationRequestDto;
import ru.practicum.ewm.service.PrivateService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestPrivateController {

    private final PrivateService privateService;


    @PostMapping(value = "/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ParticipationRequestDto createEvent(@PathVariable Long userId,
                                               @RequestParam(name = "eventId") Long eventId,
                                               HttpServletRequest request) throws Exception {
        log.debug("Получен {} запрос: {}", request.getMethod(), request.getRequestURI());
        return privateService.createRequest(userId, eventId);
    }

    @GetMapping(value = "/users/{userId}/requests")
    public List<ParticipationRequestDto> getEventsByUser(@PathVariable Long userId,
                                                         HttpServletRequest request) {
        log.debug("Получен {} запрос: {}", request.getMethod(), request.getRequestURI());
        return privateService.getRequestsByUser(userId);
    }

    @PatchMapping(value = "/users/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ParticipationRequestDto updateEvent(@PathVariable Long userId,
                                               @PathVariable Long requestId,
                                               HttpServletRequest request) {
        log.debug("Получен {} запрос: {}", request.getMethod(), request.getRequestURI());
        return privateService.updateRequest(userId, requestId);
    }
}