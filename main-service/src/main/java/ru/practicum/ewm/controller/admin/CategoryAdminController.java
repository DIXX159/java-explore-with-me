package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.service.AdminService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final AdminService adminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto, HttpServletRequest request) throws Exception {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI(), newCategoryDto);
        return adminService.createCategory(newCategoryDto);
    }

    @DeleteMapping(value = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId,
                               HttpServletRequest request) throws ConflictException {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        adminService.deleteCategory(catId);
    }

    @PatchMapping(value = "/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public Category createCategory(@PathVariable Long catId, @RequestBody @Valid NewCategoryDto newCategoryDto, HttpServletRequest request) throws Exception {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI(), newCategoryDto);
        return adminService.updateCategory(catId, newCategoryDto);
    }
}