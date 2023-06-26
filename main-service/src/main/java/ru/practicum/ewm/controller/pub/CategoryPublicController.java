package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.service.PublicService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryPublicController {

    private final PublicService publicService;

    @GetMapping
    public List<Category> getEventsByUser(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size,
                                          HttpServletRequest request) throws ValidationException {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return publicService.getCategories(PageRequest.of(from, size));
    }

    @GetMapping(value = "/{catId}")
    public Category getEventByUser(@PathVariable Long catId,
                                   HttpServletRequest request) throws ValidationException {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return publicService.getCategory(catId);
    }
}