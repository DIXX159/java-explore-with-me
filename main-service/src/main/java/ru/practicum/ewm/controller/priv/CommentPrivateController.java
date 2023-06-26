package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.service.PrivateService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentPrivateController {

    private final PrivateService privateService;


    @PostMapping(value = "/{userId}/event/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Comment createComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @RequestBody @Valid NewCommentDto newCommentDto,
                                 HttpServletRequest request) {
        log.debug("Получен {} запрос: {}", request.getMethod(), request.getRequestURI());
        return privateService.createComment(userId, eventId, newCommentDto);
    }

    @GetMapping(value = "/{userId}/comments")
    public List<Comment> getCommentsByUser(@PathVariable Long userId,
                                           HttpServletRequest request) {
        log.debug("Получен {} запрос: {}", request.getMethod(), request.getRequestURI());
        return privateService.getCommentsByUser(userId);
    }

    @GetMapping(value = "/event/{eventId}")
    public List<Comment> getCommentsByEvent(@PathVariable Long eventId,
                                            HttpServletRequest request) {
        log.debug("Получен {} запрос: {}", request.getMethod(), request.getRequestURI());
        return privateService.getCommentsByEvent(eventId);
    }

    @PatchMapping(value = "/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Comment updateCommentByUser(@PathVariable Long userId,
                                       @PathVariable Long commentId,
                                       @RequestBody @Valid NewCommentDto newCommentDto,
                                       HttpServletRequest request) {
        log.debug("Получен {} запрос: {}", request.getMethod(), request.getRequestURI());
        return privateService.updateCommentByUser(userId, commentId, newCommentDto);
    }

    @DeleteMapping(value = "/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId,
                              HttpServletRequest request) {
        log.debug("Получен {} запрос: {}", request.getMethod(), request.getRequestURI());
        privateService.deleteComment(commentId);
    }
}