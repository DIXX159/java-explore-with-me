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
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ModelMapper(CategoryRepository categoryRepository, UserRepository userRepository, CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    public CategoryDto toCategoryDto(NewCategoryDto newCategoryDto) {
        if (newCategoryDto == null) {
            return null;
        }
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(newCategoryDto.getName());
        return categoryDto;
    }

    public UserDto toUserDto(NewUserRequest newUserRequest) {
        if (newUserRequest == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setEmail(newUserRequest.getEmail());
        userDto.setName(newUserRequest.getName());
        return userDto;
    }

    public UserShortDto toUserShortDto(Optional<UserDto> userDto) {
        return new UserShortDto(
                userDto.get().getId(),
                userDto.get().getName()
        );
    }

    public EventFullEntity toEventFullDto(Long userId, NewEventDto newEventDto) {
        if (newEventDto == null) {
            return null;
        }
        EventFullEntity eventFullEntity = new EventFullEntity();
        eventFullEntity.setAnnotation(newEventDto.getAnnotation());
        eventFullEntity.setCategory(categoryRepository.findById(newEventDto.getCategory()).get());
        eventFullEntity.setConfirmedRequests(0L);
        eventFullEntity.setCreatedOn(LocalDateTime.now());
        eventFullEntity.setDescription(newEventDto.getDescription());
        eventFullEntity.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), formatter));
        eventFullEntity.setInitiator(userRepository.getReferenceById(userId));
        eventFullEntity.setLocation(newEventDto.getLocation());
        if (newEventDto.getPaid() != null) {
            eventFullEntity.setPaid(newEventDto.getPaid());
        } else {
            eventFullEntity.setPaid(false);
        }
        if (newEventDto.getPaid() != null) {
            eventFullEntity.setParticipantLimit(newEventDto.getParticipantLimit());
        } else {
            eventFullEntity.setParticipantLimit(0L);
        }
        eventFullEntity.setPublishedOn(LocalDateTime.now());
        if (newEventDto.getPaid() != null) {
            eventFullEntity.setRequestModeration(newEventDto.getRequestModeration());
        } else {
            eventFullEntity.setRequestModeration(true);
        }
        eventFullEntity.setState(State.PENDING.name());
        eventFullEntity.setTitle(newEventDto.getTitle());
        eventFullEntity.setViews(0L);
        return eventFullEntity;
    }

    public EventFullDto toEventFullDto(EventFullEntity eventFullEntity) {
        return new EventFullDto(
                eventFullEntity.getId(),
                eventFullEntity.getAnnotation(),
                categoryRepository.findById(eventFullEntity.getCategory().getId()).get(),
                eventFullEntity.getConfirmedRequests(),
                eventFullEntity.getCreatedOn().format(formatter),
                eventFullEntity.getDescription(),
                eventFullEntity.getEventDate().format(formatter),
                toUserShortDto(userRepository.findById(eventFullEntity.getInitiator().getId())),
                eventFullEntity.getLocation(),
                eventFullEntity.getPaid(),
                eventFullEntity.getParticipantLimit(),
                eventFullEntity.getPublishedOn().format(formatter),
                eventFullEntity.getRequestModeration(),
                State.from(eventFullEntity.getState()).get(),
                eventFullEntity.getTitle(),
                eventFullEntity.getViews()
        );
    }

    public EventShortDto toEventShortDto(EventFullEntity eventFullEntity) {

        return new EventShortDto(
                eventFullEntity.getAnnotation(),
                eventFullEntity.getCategory(),
                eventFullEntity.getConfirmedRequests(),
                eventFullEntity.getEventDate().format(formatter),
                eventFullEntity.getId(),
                toUserShortDto(userRepository.findById(eventFullEntity.getInitiator().getId())),
                eventFullEntity.getPaid(),
                eventFullEntity.getTitle(),
                eventFullEntity.getViews()
        );
    }

    public List<EventShortDto> mapToEventShortDto(Iterable<EventFullEntity> eventFullEntities) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        for (EventFullEntity eventFullEntity : eventFullEntities) {
            eventShortDtoList.add(toEventShortDto(eventFullEntity));
        }
        return eventShortDtoList;
    }

    public CompilationDto toCompilationDto(CompilationFullDto compilationFullDto) {
        if (compilationFullDto == null) {
            return null;
        }
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        CompilationDto compilationDto = new CompilationDto();
        for (EventFullEntity event : compilationFullDto.getEvents()) {

            eventShortDtoList.add(toEventShortDto(eventRepository.findEventFullEntityById(event.getId())));
        }
        compilationDto.setId(compilationFullDto.getId());
        compilationDto.setEvents(eventShortDtoList);
        compilationDto.setPinned(compilationFullDto.getPinned());
        compilationDto.setTitle(compilationFullDto.getTitle());
        return compilationDto;
    }

    public CompilationFullDto toCompilationFullDto(NewCompilationDto newCompilationDto) {
        if (newCompilationDto == null) {
            return null;
        }
        List<EventFullEntity> eventFullEntityList = new ArrayList<>();
        CompilationFullDto compilationFullDto = new CompilationFullDto();
        if (newCompilationDto.getEvents() != null) {
            for (Long event : newCompilationDto.getEvents()) {
                eventFullEntityList.add(eventRepository.findEventFullEntityById(event));
            }
        }
        compilationFullDto.setEvents(eventFullEntityList);
        if (newCompilationDto.getPinned() != null) {
            compilationFullDto.setPinned(newCompilationDto.getPinned());
        } else {
            compilationFullDto.setPinned(false);
        }
        compilationFullDto.setTitle(newCompilationDto.getTitle());
        return compilationFullDto;
    }

    public CompilationFullDto toCompilationFullDto(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest == null) {
            return null;
        }
        CompilationFullDto compilationFullDto = new CompilationFullDto();
        if (updateCompilationRequest.getEvents() != null) {
            List<EventFullEntity> eventFullEntityList = new ArrayList<>();
            for (Long event : updateCompilationRequest.getEvents()) {
                eventFullEntityList.add(eventRepository.findEventFullEntityById(event));
            }
            compilationFullDto.setEvents(eventFullEntityList);
        } else {
            compilationFullDto.setEvents(compilationRepository.findCompilationFullDtoById(compId).getEvents());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilationFullDto.setPinned(updateCompilationRequest.getPinned());
        } else {
            compilationFullDto.setPinned(false);
        }
        compilationFullDto.setTitle(updateCompilationRequest.getTitle());
        compilationFullDto.setId(compId);
        return compilationFullDto;
    }

    public List<CompilationDto> mapToCompilationDto(Iterable<CompilationFullDto> compilationFullDtoList) {
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (CompilationFullDto compilationFullDto : compilationFullDtoList) {
            compilationDtoList.add(toCompilationDto(compilationFullDto));
        }
        return compilationDtoList;
    }

    public List<EventFullDto> mapToEventFullDto(Iterable<EventFullEntity> eventFullEntityList) {
        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        for (EventFullEntity eventFullEntity : eventFullEntityList) {
            eventFullDtoList.add(toEventFullDto(eventFullEntity));
        }
        return eventFullDtoList;
    }
}