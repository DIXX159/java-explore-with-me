package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.StatsServiceClient;
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
@Slf4j
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final CompilationRepository compilationRepository;
    private final StatsServiceClient statClient;
    private final ModelMapper modelMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, PageRequest pageRequest) {
        log.info("Public: get Compilations by pinned {}", pinned);
        Page<Compilation> compilationFullDtoList = compilationRepository.findCompilationFullDtoList(pinned, pageRequest);
        return modelMapper.mapToCompilationDto(compilationFullDtoList);
    }

    @Override
    public CompilationDto getCompilationsById(Long compId) {
        log.info("Public: get Compilations by id {}", compId);
        return modelMapper.toCompilationDto(compilationRepository.findCompilationFullDtoById(compId));
    }

    @Override
    public List<Category> getCategories(PageRequest pageRequest) throws ValidationException {
        log.info("Public: get Categories");
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
    public Category getCategory(Long catId) {
        log.info("Public: get Category by id {}", catId);
        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found",
                "The required object was not found.",
                HttpStatus.NOT_FOUND));
    }

    @Override
    public List<EventShortDto> getFilteredEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, PageRequest pageRequest) throws ValidationException {
        log.info("Public: get filtered Events");
        Page<Event> eventFullEntities;
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
            for (Event event : eventFullEntities) {
                ResponseEntity<Object> stats = statClient.getStats(LocalDateTime.now().minusYears(1).format(formatter), LocalDateTime.now().format(formatter), new String[]{"/events/" + event.getId()}, true);
                String[] body = Objects.requireNonNull(stats.getBody()).toString().split("hits");
                if (!Objects.equals(body[0], "[]")) {
                    event.setViews(Long.valueOf(body[1].substring(body[1].indexOf('=') + 1, body[1].indexOf('}'))));
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
        log.info("Public: get Event by id {}", id);
        Event event = eventRepository.findEventFullEntityByIdAndStateLike(id, State.PUBLISHED.name()).orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found",
                "The required object was not found.",
                HttpStatus.NOT_FOUND));
        ResponseEntity<Object> stats = statClient.getStats(LocalDateTime.now().minusYears(1).format(formatter), LocalDateTime.now().format(formatter), new String[]{"/events/" + id}, true);
        String[] body = Objects.requireNonNull(stats.getBody()).toString().split("hits");
        if (!Objects.equals(body[0], "[]")) {
            event.setViews(Long.valueOf(body[1].substring(body[1].indexOf('=') + 1, body[1].indexOf('}'))));
        }
        return modelMapper.toEvent(event);
    }
}