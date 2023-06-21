package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.enums.State;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final CompilationRepository compilationRepository;
    private final StatClient statClient;
    private final ModelMapper modelMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, PageRequest pageRequest) {
        Page<CompilationFullDto> compilationFullDtoList = compilationRepository.findCompilationFullDtoList(pinned, pageRequest);
        return modelMapper.mapToCompilationDto(compilationFullDtoList);
    }

    @Override
    public CompilationDto getCompilationsById(Long compId) {
        return modelMapper.toCompilationDto(compilationRepository.findCompilationFullDtoById(compId));
    }

    @Override
    public List<CategoryDto> getCategories(PageRequest pageRequest) throws ValidationException {
        try {
            return categoryRepository.findAll(pageRequest).toList();
        } catch (Exception e) {
            throw new ValidationException(e.getMessage(),
                    "Incorrectly made request.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found",
                "The required object was not found.",
                HttpStatus.NOT_FOUND));
    }

    @Override
    public List<EventShortDto> getFilteredEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, PageRequest pageRequest) throws ValidationException {
        Page<EventFullEntity> eventFullEntities;
        if (Objects.equals(text, "0")) {
            throw new ValidationException("Event must be published",
                    "Incorrectly made request.",
                    HttpStatus.BAD_REQUEST
            );
        }
        try {
            if (rangeStart != null && rangeEnd != null) {
                if (onlyAvailable) {
                    eventFullEntities = eventRepository.findFiltredEventsOnlyAvailable(text, categories, paid, LocalDateTime.parse(rangeStart, formatter), LocalDateTime.parse(rangeEnd, formatter), pageRequest.withSort(Sort.by(Sort.Direction.ASC, "eventDate")));
                } else {
                    eventFullEntities = eventRepository.findFiltredEvents(text, categories, paid, LocalDateTime.parse(rangeStart, formatter), LocalDateTime.parse(rangeEnd, formatter), pageRequest.withSort(Sort.by(Sort.Direction.ASC, "eventDate")));
                }
            } else {
                if (onlyAvailable) {
                    eventFullEntities = eventRepository.findFiltredEventsOnlyAvailable(text, categories, paid, null, null, pageRequest.withSort(Sort.by(Sort.Direction.ASC, "eventDate")));
                } else {
                    eventFullEntities = eventRepository.findFiltredEvents(text, categories, paid, null, null, pageRequest.withSort(Sort.by(Sort.Direction.ASC, "eventDate")));
                }
            }
            for (EventFullEntity eventFullEntity : eventFullEntities) {
                ResponseEntity<Object> stats = statClient.getStats(LocalDateTime.now().minusYears(1).format(formatter), LocalDateTime.now().format(formatter), new String[]{"/events/" + eventFullEntity.getId()}, true);
                String[] body = Objects.requireNonNull(stats.getBody()).toString().split("hits");
                if (!Objects.equals(body[0], "[]")) {
                    eventFullEntity.setViews(Long.valueOf(body[1].substring(body[1].indexOf('=') + 1, body[1].indexOf('}'))));
                }
            }
            return modelMapper.mapToEventShortDto(eventFullEntities);
        } catch (Exception e) {
            throw new ValidationException(e.getMessage(),
                    "Incorrectly made request.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @Override
    public EventFullDto getEventById(Long id) {
        EventFullEntity eventFullEntity = eventRepository.findEventFullEntityByIdAndStateLike(id, State.PUBLISHED.name()).orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found",
                "The required object was not found.",
                HttpStatus.NOT_FOUND));
        ResponseEntity<Object> stats = statClient.getStats(LocalDateTime.now().minusYears(1).format(formatter), LocalDateTime.now().format(formatter), new String[]{"/events/" + id}, true);
        String[] body = Objects.requireNonNull(stats.getBody()).toString().split("hits");
        if (!Objects.equals(body[0], "[]")) {
            eventFullEntity.setViews(Long.valueOf(body[1].substring(body[1].indexOf('=') + 1, body[1].indexOf('}'))));
        }
        return modelMapper.toEventFullDto(eventFullEntity);
    }
}