package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.dto.enums.State;
import ru.practicum.ewm.dto.enums.StateAction;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;
    private final CompilationRepository compilationRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Category createCategory(NewCategoryDto newCategoryDto) throws ValidationException, ConflictException {
        log.info("Admin: create new Category");
        if (newCategoryDto.getName() != null) {
            try {
                Category category = modelMapper.toCategory(newCategoryDto);
                return categoryRepository.save(category);
            } catch (ConstraintViolationException e) {
                throw new ConflictException(e.getConstraintName(),
                        "Integrity constraint has been violated.",
                        HttpStatus.CONFLICT
                );
            }
        } else throw new ValidationException(
                "Field: name. Error: must not be blank. Value: null",
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public void deleteCategory(Long catId) throws ConflictException {
        log.info("Admin: delete Category by id {}", catId);
        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        if (eventRepository.findEventFullEntityByCategory(catId).isEmpty()) {
            categoryRepository.deleteById(catId);
        } else throw new ConflictException("The category is not empty",
                "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT
        );
    }


    @Override
    public Category updateCategory(Long catId, NewCategoryDto newCategoryDto) throws ConflictException {
        log.info("Admin: update Category by id {}", catId);
        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        try {
            Category category = modelMapper.toCategory(newCategoryDto);
            category.setId(catId);
            categoryRepository.updateCategory(catId, category.getName());
            return categoryRepository.save(category);
        } catch (ConstraintViolationException e) {
            throw new ConflictException(e.getConstraintName(),
                    "Integrity constraint has been violated.",
                    HttpStatus.CONFLICT
            );
        }
    }

    @Override
    public User createUser(NewUserRequest newUserRequest) throws ConflictException {
        log.info("Admin: create new User");
        try {
            User user = modelMapper.toUser(newUserRequest);
            return userRepository.save(user);
        } catch (ConstraintViolationException e) {
            throw new ConflictException(e.getConstraintName(),
                    "Integrity constraint has been violated.",
                    HttpStatus.CONFLICT
            );
        }
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Admin: delete User by id {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        userRepository.deleteById(userId);
    }


    @Override
    public List<User> getUsers(List<Long> ids, PageRequest pageRequest) {
        log.info("Admin: get Users by ids");
        if (ids != null) {
            return userRepository.getUserDtoListByIdIsIn(ids, pageRequest).toList();
        } else {
            return userRepository.findAll(pageRequest).toList();
        }
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) throws ConflictException {
        log.info("Admin: create new Compilation");
        try {
            return modelMapper.toCompilationDto(compilationRepository.save(modelMapper.toCompilationFullDto(newCompilationDto)));
        } catch (ConstraintViolationException e) {
            throw new ConflictException(e.getConstraintName(),
                    "Integrity constraint has been violated.",
                    HttpStatus.CONFLICT
            );
        }
    }

    @Override
    public void deleteCompilation(Long compId) {
        log.info("Admin: delete Compilation by id {}", compId);
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("User with id=" + compId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Admin: update Compilation by id {}", compId);
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("User with id=" + compId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        if (updateCompilationRequest.getEvents() != null && updateCompilationRequest.getEvents().size() != 0) {
            List<Event> eventsFromRepository = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(eventsFromRepository);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        } else {
            compilation.setPinned(false);
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        return modelMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, PageRequest pageRequest) {
        log.info("Admin: get Events");
        Iterable<Event> eventFullEntities;
        if (rangeStart != null && rangeEnd != null) {
            eventFullEntities = eventRepository.findEventsAdmin(users, states, categories, LocalDateTime.parse(rangeStart, formatter), LocalDateTime.parse(rangeEnd, formatter), pageRequest);
        } else {
            eventFullEntities = eventRepository.findEventsAdmin(users, states, categories, null, null, pageRequest);

        }
        return modelMapper.mapToEventFullDto(eventFullEntities);

    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) throws ConflictException, ValidationException {
        log.info("Admin: update Event by id {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));

        if (updateEventAdminRequest.getStateAction() != null && updateEventAdminRequest.getStateAction() == StateAction.PUBLISH_EVENT && !Objects.equals(event.getState(), State.PENDING.name())) {
            throw new ConflictException("Event state is not PENDING",
                    "For the requested operation the conditions are not met.",
                    HttpStatus.CONFLICT);
        }
        if (updateEventAdminRequest.getStateAction() != null && updateEventAdminRequest.getStateAction() == StateAction.REJECT_EVENT && Objects.equals(event.getState(), State.PUBLISHED.name())) {
            throw new ConflictException("Event state is PUBLISHED",
                    "For the requested operation the conditions are not met.",
                    HttpStatus.CONFLICT);
        }

        if (updateEventAdminRequest.getEventDate() != null && LocalDateTime.parse(updateEventAdminRequest.getEventDate(), formatter).isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Invalid event date",
                    "For the requested operation the conditions are not met.",
                    HttpStatus.BAD_REQUEST);
        }

        if (updateEventAdminRequest.getAnnotation() != null)
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        if (updateEventAdminRequest.getCategory() != null)
            event.setCategory(categoryRepository.getReferenceById(updateEventAdminRequest.getCategory()));
        if (updateEventAdminRequest.getDescription() != null)
            event.setDescription(updateEventAdminRequest.getDescription());
        if (updateEventAdminRequest.getEventDate() != null)
            event.setEventDate(LocalDateTime.parse(updateEventAdminRequest.getEventDate(), formatter));
        if (updateEventAdminRequest.getLocation() != null) event.setLocation(updateEventAdminRequest.getLocation());
        if (updateEventAdminRequest.getPaid() != null) event.setPaid(updateEventAdminRequest.getPaid());
        if (updateEventAdminRequest.getParticipantLimit() != null)
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        if (updateEventAdminRequest.getRequestModeration() != null)
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        if (updateEventAdminRequest.getStateAction() == StateAction.PUBLISH_EVENT)
            event.setState(State.PUBLISHED.name());
        if (updateEventAdminRequest.getStateAction() == StateAction.REJECT_EVENT) event.setState(State.CANCELED.name());
        if (updateEventAdminRequest.getTitle() != null) event.setTitle(updateEventAdminRequest.getTitle());
        locationRepository.save(event.getLocation());
        eventRepository.save(event);
        return modelMapper.toEvent(eventRepository.save(event));
    }
}