package ru.practicum.ewm.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.model.CompilationFullDto;

import java.util.List;

public interface PublicService {

    List<CompilationDto> getCompilations(Boolean pinned, PageRequest pageRequest);

    CompilationDto getCompilationsById(Long compId);
}