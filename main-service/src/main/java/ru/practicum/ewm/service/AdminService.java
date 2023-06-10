package ru.practicum.ewm.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.CategoryDto;
import ru.practicum.ewm.model.UserDto;

import java.util.List;

public interface AdminService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto) throws Exception, ConflictException;

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto) throws ConflictException;

    UserDto createUser(NewUserRequest newUserRequest) throws ConflictException, ValidationException;

    void deleteUser(Long userId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto) throws ConflictException;

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);

    List<UserDto> getUsers(List<Long> ids, PageRequest pageRequest);
}