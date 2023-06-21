package ru.practicum.ewm.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.ParticipationRequestDto;

import java.util.List;

public interface PrivateService {

    EventFullDto createEvent(Long userId, NewEventDto newEventDto) throws ConflictException, ValidationException;

    List<EventShortDto> getEventsByUser(Long userId, PageRequest pageRequest) throws ValidationException;

    EventFullDto getEventByUser(Long userId, Long eventId) throws ValidationException;

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) throws ConflictException, ValidationException;

    ParticipationRequestDto createRequest(Long userId, Long eventId) throws ConflictException;

    List<ParticipationRequestDto> getRequestsByUser(Long userId);

    ParticipationRequestDto updateRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsOnEventByUser(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) throws ConflictException;
}