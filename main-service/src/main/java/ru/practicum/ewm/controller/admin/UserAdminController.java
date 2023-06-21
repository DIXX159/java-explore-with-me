package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.model.UserDto;
import ru.practicum.ewm.service.AdminService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserAdminController {

    private final AdminService adminService;

    @PostMapping(value = "/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserDto createUser(@RequestBody @Valid NewUserRequest newUserRequest, HttpServletRequest request) throws Exception {
        log.debug("Получен {} запрос {} тело запроса: {}", request.getMethod(), request.getRequestURI(), newUserRequest);
        return adminService.createUser(newUserRequest);
    }

    @DeleteMapping(value = "/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteUser(@PathVariable Long userId,
                           HttpServletRequest request) {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        adminService.deleteUser(userId);
    }

    @GetMapping(value = "/admin/users")
    public List<UserDto> getEventsByUser(@RequestParam(required = false) List<Long> ids,
                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @RequestParam(name = "size", defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        log.debug("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        return adminService.getUsers(ids, PageRequest.of(from, size));
    }
}