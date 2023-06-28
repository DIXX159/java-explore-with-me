package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.NewCommentStatusDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.service.AdminService;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentAdminController {

    private final AdminService adminService;

    @PatchMapping(value = "/{comId}")
    @ResponseStatus(HttpStatus.OK)
    public Comment updateAdminComment(@PathVariable Long comId,
                                      @RequestBody NewCommentStatusDto newCommentStatusDto,
                                      HttpServletRequest request) throws ConflictException {
        log.debug("Получен {} запрос: {}", request.getMethod(), request.getRequestURI());
        return adminService.updateAdminComment(comId, newCommentStatusDto);
    }
}