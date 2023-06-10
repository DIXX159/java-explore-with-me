package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CompilationRepository compilationRepository;
    private final ParticipationRequestRepository participationRequestRepository;
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
}