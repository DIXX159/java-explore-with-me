package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.NewCategoryDto;
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
public class CategoryAdminController {

    private final AdminService adminService;

    @PostMapping(value = "/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto, HttpServletRequest request) throws Exception {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI(), newCategoryDto);
        return adminService.createCategory(newCategoryDto);
    }

    @DeleteMapping(value = "/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteCategory(@PathVariable Long catId,
                               HttpServletRequest request) throws ConflictException {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        adminService.deleteCategory(catId);
    }

    @PatchMapping(value = "/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CategoryDto createCategory(@PathVariable Long catId, @RequestBody @Valid NewCategoryDto newCategoryDto, HttpServletRequest request) throws Exception {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI(), newCategoryDto);
        return adminService.updateCategory(catId, newCategoryDto);
    }
}