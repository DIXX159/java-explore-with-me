package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class PrivateServiceImpl implements PrivateService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) throws ValidationException {
        log.info("Private: create new Event by User {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        if (LocalDateTime.parse(newEventDto.getEventDate(), formatter).isAfter(LocalDateTime.now().plusHours(2))) {
            try {
                Event event = modelMapper.toEvent(userId, newEventDto);
                locationRepository.save(event.getLocation());
                Event event1 = eventRepository.save(event);
                return modelMapper.toEvent(event1);
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
        log.info("Private: get Events by User {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        List<Event> ev = eventRepository.findEventsByUser(userId, pageRequest).getContent();
        return modelMapper.mapToEventShortDto(ev);
    }


    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        log.info("Private: get Event by User {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        return modelMapper.toEvent(eventRepository.findByInitiatorAndId(user, eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND)));
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) throws ConflictException, ValidationException {
        log.info("Private: update for event id {} by user id {}", eventId, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        Event event = eventRepository.findById(eventId)
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

        return modelMapper.toEvent(eventRepository.findEventByInitiatorAndId(userRepository.getReferenceById(userId), eventId));
    }

    @Override
    public ParticipationRequest createRequest(Long userId, Long eventId) throws ConflictException {
        log.info("Private: create new Request for Event {} by User {}", eventId, userId);
        Event event = eventRepository.findById(eventId)
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
            return participationRequestRepository.save(new ParticipationRequest(null, LocalDateTime.now().format(formatter), eventId, userId, Status.CONFIRMED.name()));
        } else {
            return participationRequestRepository.save(new ParticipationRequest(null, LocalDateTime.now().format(formatter), eventId, userId, State.PENDING.name()));
        }
    }

    @Override
    public List<ParticipationRequest> getRequestsByUser(Long userId) {
        log.info("Private: get Requests by User {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));

        return participationRequestRepository.getParticipationRequestDtosByRequester(userId);
    }

    @Override
    public ParticipationRequest updateRequest(Long userId, Long requestId) {
        log.info("Private: update Request {} by User {}", requestId, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));

        participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + requestId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));

        ParticipationRequest request = participationRequestRepository.getParticipationRequestDtoByIdAndRequester(requestId, userId);
        request.setStatus(State.CANCELED.name());
        participationRequestRepository.updateRequest(requestId, userId, State.CANCELED.name());
        return participationRequestRepository.save(request);
    }

    @Override
    public List<ParticipationRequest> getRequestsOnEventByUser(Long userId, Long eventId) {
        log.info("Private: get Request on Event {} by User {}", eventId, userId);
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        return participationRequestRepository.getParticipationRequestDtosByEvent(eventId);
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) throws ConflictException {
        log.info("Private: update Request status for Event {} by User {}", eventId, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        List<ParticipationRequest> participationRequestList = participationRequestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());
        for (ParticipationRequest participationRequest : participationRequestList) {
            if (!Objects.equals(participationRequest.getStatus(), State.PENDING.name())) {
                throw new ConflictException("Status is not PENDING", "For the requested operation the conditions are not met.", HttpStatus.CONFLICT);
            }
            if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
                throw new ConflictException("The participant limit has been reached", "For the requested operation the conditions are not met.", HttpStatus.CONFLICT);
            }
            participationRequestRepository.updateRequest(participationRequest.getId(), userId, eventRequestStatusUpdateRequest.getStatus().name());
            participationRequestRepository.save(new ParticipationRequest(participationRequest.getId(), participationRequest.getCreated(), participationRequest.getEvent(), participationRequest.getRequester(), eventRequestStatusUpdateRequest.getStatus().name()));
            eventRepository.updateRequest(eventId, event.getConfirmedRequests() + 1);
        }
        return new EventRequestStatusUpdateResult(participationRequestRepository.getParticipationRequestDtosByEventAndStatus(eventId, Status.CONFIRMED.name()), participationRequestRepository.getParticipationRequestDtosByEventAndStatus(eventId, Status.REJECTED.name()));
    }

    @Override
    public Comment createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("Private: create Comment for Event {} by User {}", eventId, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        Comment entity = modelMapper.toComment(userId, eventId, newCommentDto);
        return commentRepository.save(entity);
    }

    @Override
    public List<Comment> getCommentsByUser(Long userId) {
        log.info("Private: get Comments by User {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        return commentRepository.findAllByCommentator(userId);
    }

    public List<Comment> getCommentsByEvent(Long eventId) {
        log.info("Private: get Comments by Event {}", eventId);
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        return commentRepository.findAllByEventIdAndState(eventId, State.PUBLISHED.name());
    }

    @Override
    public Comment updateCommentByUser(Long userId, Long commentId, NewCommentDto newCommentDto) {
        log.info("Private: update Comment {} by User {}", commentId, userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        comment.setId(commentId);
        comment.setComment(newCommentDto.getComment());
        comment.setState(State.PENDING.name());
        commentRepository.updateComment(commentId, newCommentDto.getComment(), comment.getState());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        log.info("Private: delete Comment by id {}", commentId);
        commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        commentRepository.deleteById(commentId);
    }
}