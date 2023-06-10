package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.model.CategoryDto;
import ru.practicum.ewm.service.AdminService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationAdminController {

    private final AdminService adminService;
    //private final StatsClient statsClient;

    @PostMapping(value = "/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto, HttpServletRequest request) throws ConflictException {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI(), newCompilationDto);
        return adminService.createCompilation(newCompilationDto);
        }

    @DeleteMapping(value = "/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteCompilation(@PathVariable Long compId,
                                             HttpServletRequest request) {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
            adminService.deleteCompilation(compId);
    }

    @PatchMapping(value = "/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CompilationDto updateCompilation(@PathVariable Long compId, @RequestBody UpdateCompilationRequest updateCompilationRequest, HttpServletRequest request) {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI(), updateCompilationRequest);
        return adminService.updateCompilation(compId, updateCompilationRequest);
    }
}