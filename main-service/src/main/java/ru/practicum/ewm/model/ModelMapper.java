package ru.practicum.ewm.model;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.dto.enums.State;
import ru.practicum.ewm.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ModelMapper {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ModelMapper(CategoryRepository categoryRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public Category toCategory(NewCategoryDto newCategoryDto) {
        if (newCategoryDto == null) {
            return null;
        }
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return category;
    }

    public User toUser(NewUserRequest newUserRequest) {
        if (newUserRequest == null) {
            return null;
        }
        User user = new User();
        user.setEmail(newUserRequest.getEmail());
        user.setName(newUserRequest.getName());
        return user;
    }

    public UserShortDto toUserShortDto(Optional<User> userDto) {
        return new UserShortDto(
                userDto.get().getId(),
                userDto.get().getName()
        );
    }

    public Event toEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto == null) {
            return null;
        }
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(categoryRepository.findById(newEventDto.getCategory()).get());
        event.setConfirmedRequests(0L);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), formatter));
        event.setInitiator(userRepository.getReferenceById(userId));
        event.setLocation(newEventDto.getLocation());
        if (newEventDto.getPaid() != null) {
            event.setPaid(newEventDto.getPaid());
        } else {
            event.setPaid(false);
        }
        if (newEventDto.getPaid() != null) {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        } else {
            event.setParticipantLimit(0L);
        }
        event.setPublishedOn(LocalDateTime.now());
        if (newEventDto.getPaid() != null) {
            event.setRequestModeration(newEventDto.getRequestModeration());
        } else {
            event.setRequestModeration(true);
        }
        event.setState(State.PENDING.name());
        event.setTitle(newEventDto.getTitle());
        event.setViews(0L);
        return event;
    }

    public EventFullDto toEvent(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                categoryRepository.findById(event.getCategory().getId()).get(),
                event.getConfirmedRequests(),
                event.getCreatedOn().format(formatter),
                event.getDescription(),
                event.getEventDate().format(formatter),
                toUserShortDto(userRepository.findById(event.getInitiator().getId())),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn().format(formatter),
                event.getRequestModeration(),
                State.from(event.getState()).get(),
                event.getTitle(),
                event.getViews()
        );
    }

    public EventShortDto toEventShortDto(Event event) {

        return new EventShortDto(
                event.getAnnotation(),
                event.getCategory(),
                event.getConfirmedRequests(),
                event.getEventDate().format(formatter),
                event.getId(),
                toUserShortDto(userRepository.findById(event.getInitiator().getId())),
                event.getPaid(),
                event.getTitle(),
                event.getViews()
        );
    }

    public List<EventShortDto> mapToEventShortDto(Iterable<Event> eventFullEntities) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        for (Event event : eventFullEntities) {
            eventShortDtoList.add(toEventShortDto(event));
        }
        return eventShortDtoList;
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        if (compilation == null) {
            return null;
        }
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        CompilationDto compilationDto = new CompilationDto();
        for (Event event : compilation.getEvents()) {

            eventShortDtoList.add(toEventShortDto(eventRepository.findEventFullEntityById(event.getId())));
        }
        compilationDto.setId(compilation.getId());
        compilationDto.setEvents(eventShortDtoList);
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        return compilationDto;
    }

    public Compilation toCompilationFullDto(NewCompilationDto newCompilationDto) {
        if (newCompilationDto == null) {
            return null;
        }
        List<Event> eventList = new ArrayList<>();
        Compilation compilation = new Compilation();
        if (newCompilationDto.getEvents() != null) {
            for (Long event : newCompilationDto.getEvents()) {
                eventList.add(eventRepository.findEventFullEntityById(event));
            }
        }
        compilation.setEvents(eventList);
        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        } else {
            compilation.setPinned(false);
        }
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }

    public List<CompilationDto> mapToCompilationDto(Iterable<Compilation> compilationFullDtoList) {
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilationFullDtoList) {
            compilationDtoList.add(toCompilationDto(compilation));
        }
        return compilationDtoList;
    }

    public List<EventFullDto> mapToEventFullDto(Iterable<Event> eventFullEntityList) {
        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        for (Event event : eventFullEntityList) {
            eventFullDtoList.add(toEvent(event));
        }
        return eventFullDtoList;
    }

    public Comment toComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        return new Comment(
                null,
                newCommentDto.getComment(),
                userId,
                State.PENDING.name(),
                eventId
        );
    }
}