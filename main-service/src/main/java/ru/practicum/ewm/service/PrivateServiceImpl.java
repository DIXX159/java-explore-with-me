package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.dto.enums.State;
import ru.practicum.ewm.dto.enums.StateAction;
import ru.practicum.ewm.dto.enums.Status;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PrivateServiceImpl implements PrivateService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final ModelMapper modelMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) throws ValidationException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        if (LocalDateTime.parse(newEventDto.getEventDate(), formatter).isAfter(LocalDateTime.now().plusHours(2))) {
            try {
                EventFullEntity eventFullEntity = modelMapper.toEventFullDto(userId, newEventDto);
                locationRepository.save(eventFullEntity.getLocation());
                EventFullEntity eventFullEntity1 = eventRepository.save(eventFullEntity);
                return modelMapper.toEventFullDto(eventFullEntity1);
            } catch (Exception e) {
                throw new ValidationException(e.getMessage(),
                        "Incorrectly made request.",
                        HttpStatus.BAD_REQUEST
                );
            }
        } else
            throw new ValidationException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + newEventDto.getEventDate(),
                    "Integrity constraint has been violated.",
                    HttpStatus.CONFLICT
            );
    }

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, PageRequest pageRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        List<EventFullEntity> ev = eventRepository.findEventsByUser(userId, pageRequest).getContent();
        return modelMapper.mapToEventShortDto(ev);
    }


    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        UserDto userDto = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        return modelMapper.toEventFullDto(eventRepository.findByInitiatorAndId(userDto, eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND)));
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) throws ConflictException, ValidationException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        EventFullEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        if (event.getState().equals(State.PUBLISHED.name()))
            throw new ConflictException("Cannot publish the event because it's not in the right state: PUBLISHED",
                    "For the requested operation the conditions are not met.",
                    HttpStatus.CONFLICT);

        if (updateEventUserRequest.getAnnotation() != null) event.setAnnotation(updateEventUserRequest.getAnnotation());
        if (updateEventUserRequest.getCategory() != null)
            event.setCategory(categoryRepository.getReferenceById(updateEventUserRequest.getCategory()));
        if (updateEventUserRequest.getDescription() != null)
            event.setDescription(updateEventUserRequest.getDescription());
        if (updateEventUserRequest.getEventDate() != null) {
            if (LocalDateTime.parse(updateEventUserRequest.getEventDate(), formatter).minusHours(2).isBefore(LocalDateTime.now()) ||
                    event.getEventDate().minusHours(2).isBefore(LocalDateTime.now()))
                throw new ValidationException("Less than 2 hours before the event",
                        "For the requested operation the conditions are not met.",
                        HttpStatus.CONFLICT);
            event.setEventDate(LocalDateTime.parse(updateEventUserRequest.getEventDate(), formatter));
        }
        if (updateEventUserRequest.getLocation() != null)
            event.setLocation(updateEventUserRequest.getLocation());
        if (updateEventUserRequest.getPaid() != null) event.setPaid(updateEventUserRequest.getPaid());
        if (updateEventUserRequest.getParticipantLimit() != null)
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        if (updateEventUserRequest.getRequestModeration() != null)
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        if (updateEventUserRequest.getStateAction() != null) {
            if (Objects.equals(event.getState(), State.PUBLISHED.name()))
                throw new ConflictException("Cannot publish the event because it's not in the right state: PUBLISHED",
                        "For the requested operation the conditions are not met.",
                        HttpStatus.CONFLICT);
            if (updateEventUserRequest.getStateAction().equals(StateAction.CANCEL_REVIEW))
                event.setState(State.CANCELED.name());
            if (updateEventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW))
                event.setState(State.PENDING.name());
        }
        if (updateEventUserRequest.getTitle() != null) event.setTitle(updateEventUserRequest.getTitle());
        eventRepository.save(event);

        return modelMapper.toEventFullDto(eventRepository.findEventByInitiatorAndId(userRepository.getReferenceById(userId), eventId));
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) throws ConflictException {
        EventFullEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));

        if (participationRequestRepository.getParticipationRequestDtoByEventAndRequester(eventId, userId).isPresent()) {
            throw new ConflictException("Can't add a repeat request",
                    "Request already added",
                    HttpStatus.CONFLICT);
        }

        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("The initiator of the event cannot add a request to participate in his event",
                    "Requester is initiator",
                    HttpStatus.CONFLICT);
        }
        if (!Objects.equals(event.getState(), State.PUBLISHED.name())) {
            throw new ConflictException("You can't participate in an unpublished event",
                    "Event is not published",
                    HttpStatus.CONFLICT);
        }

        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("The limit of requests for participation exceeded",
                    "Participants limit is exceeded",
                    HttpStatus.CONFLICT);
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.updateRequest(eventId, event.getConfirmedRequests());
            eventRepository.save(event);
            return participationRequestRepository.save(new ParticipationRequestDto(null, LocalDateTime.now().format(formatter), eventId, userId, Status.CONFIRMED.name()));
        } else {
            return participationRequestRepository.save(new ParticipationRequestDto(null, LocalDateTime.now().format(formatter), eventId, userId, State.PENDING.name()));
        }
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));

        return participationRequestRepository.getParticipationRequestDtosByRequester(userId);
    }

    @Override
    public ParticipationRequestDto updateRequest(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));

        participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + requestId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));

        ParticipationRequestDto request = participationRequestRepository.getParticipationRequestDtoByIdAndRequester(requestId, userId);
        request.setStatus(State.CANCELED.name());
        participationRequestRepository.updateRequest(requestId, userId, State.CANCELED.name());
        return participationRequestRepository.save(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOnEventByUser(Long userId, Long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        return participationRequestRepository.getParticipationRequestDtosByEvent(eventId);
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) throws ConflictException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));

        EventFullEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        for (Long requestId : eventRequestStatusUpdateRequest.getRequestIds()) {
            if (!Objects.equals(participationRequestRepository.findById(requestId).get().getStatus(), State.PENDING.name())) {
                throw new ConflictException("Status is not PENDING", "For the requested operation the conditions are not met.", HttpStatus.CONFLICT);
            }
            if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
                throw new ConflictException("The participant limit has been reached", "For the requested operation the conditions are not met.", HttpStatus.CONFLICT);
            }
            ParticipationRequestDto participationRequestDto = participationRequestRepository.findById(requestId).get();
            participationRequestRepository.updateRequest(requestId, userId, eventRequestStatusUpdateRequest.getStatus().name());
            participationRequestRepository.save(new ParticipationRequestDto(requestId, participationRequestDto.getCreated(), participationRequestDto.getEvent(), participationRequestDto.getRequester(), eventRequestStatusUpdateRequest.getStatus().name()));
            eventRepository.updateRequest(eventId, event.getConfirmedRequests() + 1);
        }
        return new EventRequestStatusUpdateResult(participationRequestRepository.getParticipationRequestDtosByEventAndStatus(eventId, Status.CONFIRMED.name()), participationRequestRepository.getParticipationRequestDtosByEventAndStatus(eventId, Status.REJECTED.name()));
    }
}