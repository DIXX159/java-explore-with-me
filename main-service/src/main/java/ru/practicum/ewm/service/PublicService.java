package ru.practicum.ewm.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.CategoryDto;

import java.util.List;

public interface PublicService {

    List<CompilationDto> getCompilations(Boolean pinned, PageRequest pageRequest);

    CompilationDto getCompilationsById(Long compId);

    List<CategoryDto> getCategories(PageRequest of) throws ValidationException;

    CategoryDto getCategory(Long catId) throws ValidationException;

    List<EventShortDto> getFilteredEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, PageRequest of) throws ValidationException;

    EventFullDto getEventById(Long id) throws ValidationException;
}