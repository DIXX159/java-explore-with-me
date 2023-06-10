package ru.practicum.ewm.model;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.dto.enums.State;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.UserRepository;

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
    private final LocationRepository locationRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ModelMapper(CategoryRepository categoryRepository, UserRepository userRepository, EventRepository eventRepository, LocationRepository locationRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
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

    public LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }

    public EventFullEntity toEventFullDto(Long userId, NewEventDto newEventDto) {
        if (newEventDto == null) {
            return null;
        }
        EventFullEntity eventFullEntity = new EventFullEntity();
        eventFullEntity.setAnnotation(newEventDto.getAnnotation());
        eventFullEntity.setCategory(newEventDto.getCategory());
        eventFullEntity.setConfirmedRequests(0L);
        eventFullEntity.setCreatedOn(LocalDateTime.now());
        eventFullEntity.setDescription(newEventDto.getDescription());
        eventFullEntity.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), formatter));
        eventFullEntity.setInitiator(userId);
        eventFullEntity.setPaid(newEventDto.getPaid());
        eventFullEntity.setParticipantLimit(newEventDto.getParticipantLimit());
        eventFullEntity.setPublishedOn(LocalDateTime.now());
        eventFullEntity.setRequestModeration(newEventDto.getRequestModeration());
        eventFullEntity.setState(State.PENDING);
        eventFullEntity.setTitle(newEventDto.getTitle());
        eventFullEntity.setViews(0L);
        return eventFullEntity;
    }

    public EventFullDto toEventFullDto(EventFullEntity eventFullEntity) {
        return new EventFullDto(
                eventFullEntity.getId(),
                eventFullEntity.getAnnotation(),
                categoryRepository.findById(eventFullEntity.getCategory()).get(),
                eventFullEntity.getConfirmedRequests(),
                eventFullEntity.getCreatedOn().format(formatter),
                eventFullEntity.getDescription(),
                eventFullEntity.getEventDate().format(formatter),
                toUserShortDto(userRepository.findById(eventFullEntity.getInitiator())),
                toLocationDto(locationRepository.findById(eventFullEntity.getLocation()).get()),
                eventFullEntity.getPaid(),
                eventFullEntity.getParticipantLimit(),
                eventFullEntity.getPublishedOn().format(formatter),
                eventFullEntity.getRequestModeration(),
                eventFullEntity.getState(),
                eventFullEntity.getTitle(),
                eventFullEntity.getViews()
        );
    }

    public EventShortDto toEventShortDto(EventFullEntity eventFullEntity) {

        return new EventShortDto(
                eventFullEntity.getAnnotation(),
                categoryRepository.findById(eventFullEntity.getCategory()).get(),
                eventFullEntity.getConfirmedRequests(),
                eventFullEntity.getEventDate().format(formatter),
                eventFullEntity.getId(),
                toUserShortDto(userRepository.findById(eventFullEntity.getInitiator())),
                eventFullEntity.getPaid(),
                eventFullEntity.getTitle(),
                eventFullEntity.getViews()
        );
    }

    public List<EventShortDto> mapToEventShortDto(Iterable<EventFullEntity> eventFullEntities) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        for (EventFullEntity eventFullEntity: eventFullEntities) {
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
        for (EventFullEntity event: compilationFullDto.getEvents()){

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
        for (Long event: newCompilationDto.getEvents()){

            eventFullEntityList.add(eventRepository.findEventFullEntityById(event));
        }
        compilationFullDto.setEvents(eventFullEntityList);
        compilationFullDto.setPinned(newCompilationDto.getPinned());
        compilationFullDto.setTitle(newCompilationDto.getTitle());
        return compilationFullDto;
    }

    public CompilationFullDto toCompilationFullDto(UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest == null) {
            return null;
        }
        List<EventFullEntity> eventFullEntityList = new ArrayList<>();
        CompilationFullDto compilationFullDto = new CompilationFullDto();
        for (Long event: updateCompilationRequest.getEvents()){

            eventFullEntityList.add(eventRepository.findEventFullEntityById(event));
        }
        compilationFullDto.setEvents(eventFullEntityList);
        compilationFullDto.setPinned(updateCompilationRequest.getPinned());
        compilationFullDto.setTitle(updateCompilationRequest.getTitle());
        return compilationFullDto;
    }

    public List<CompilationDto> mapToCompilationDto(Iterable<CompilationFullDto> compilationFullDtoList) {
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (CompilationFullDto compilationFullDto: compilationFullDtoList) {
            compilationDtoList.add(toCompilationDto(compilationFullDto));
        }
        return compilationDtoList;
    }
}