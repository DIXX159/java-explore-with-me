package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.service.PublicService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationPublicController {

    private final PublicService publicService;

    @GetMapping(value = "/compilations")
    public List<CompilationDto> getEventsByUser(@RequestParam(name = "pinned", defaultValue = "false") Boolean pinned,
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                HttpServletRequest request) {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return publicService.getCompilations(pinned, PageRequest.of(from, size));
    }

    @GetMapping(value = "/compilations/{compId}")
    public CompilationDto getEventByUser(@PathVariable Long compId,
                                         HttpServletRequest request) {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return publicService.getCompilationsById(compId);
    }
}