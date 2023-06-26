package ru.practicum.ewm.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.User;

import java.util.List;

public interface AdminService {

    Category createCategory(NewCategoryDto newCategoryDto) throws Exception;

    void deleteCategory(Long catId) throws ConflictException;

    Category updateCategory(Long catId, NewCategoryDto newCategoryDto) throws ConflictException;

    User createUser(NewUserRequest newUserRequest) throws ConflictException, ValidationException;

    void deleteUser(Long userId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto) throws ConflictException;

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);

    List<User> getUsers(List<Long> ids, PageRequest pageRequest);

    List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, PageRequest of);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) throws ConflictException, ValidationException;

    Comment updateAdminComment(Long comId, NewCommentStatusDto newCommentStatusDto) throws ConflictException;
}