package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.ParticipationRequestDto;
import ru.practicum.ewm.service.PrivateService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {

    private final PrivateService privateService;

    @PostMapping(value = "/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public EventFullDto createEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto, HttpServletRequest request) throws Exception {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI(), newEventDto);
        return privateService.createEvent(userId, newEventDto);
    }

    @GetMapping(value = "/users/{userId}/events")
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") Integer size,
                                               HttpServletRequest request) throws ValidationException {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return privateService.getEventsByUser(userId, PageRequest.of(from, size));
    }

    @GetMapping(value = "/users/{userId}/events/{eventId}")
    public EventFullDto getEventByUser(@PathVariable Long userId,
                                       @PathVariable Long eventId,
                                       HttpServletRequest request) throws ValidationException {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return privateService.getEventByUser(userId, eventId);
    }

    @PatchMapping(value = "/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest,
                                    HttpServletRequest request) throws ConflictException, ValidationException {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI(), updateEventUserRequest);
        return privateService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping(value = "/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsOnEventByUser(@PathVariable Long userId,
                                                                  @PathVariable Long eventId,
                                                                  HttpServletRequest request) {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return privateService.getRequestsOnEventByUser(userId, eventId);
    }

    @PatchMapping(value = "/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public EventRequestStatusUpdateResult updateRequest(@PathVariable Long userId,
                                                        @PathVariable Long eventId,
                                                        @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                        HttpServletRequest request) throws ConflictException {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI(), eventRequestStatusUpdateRequest);
        return privateService.updateStatusRequest(userId, eventId, eventRequestStatusUpdateRequest);
    }
}