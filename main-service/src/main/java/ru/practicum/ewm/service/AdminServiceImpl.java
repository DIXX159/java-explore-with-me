package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private final CompilationRepository compilationRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) throws ValidationException, ConflictException {
        if (newCategoryDto.getName() != null) {
            try {
                CategoryDto categoryDto = modelMapper.toCategoryDto(newCategoryDto);
                return categoryRepository.save(categoryDto);
            } catch (ConstraintViolationException e) {
                throw new ConflictException(e.getConstraintName(),
                        "Integrity constraint has been violated.",
                        HttpStatus.CONFLICT
                );
            }
        } else throw new ValidationException(
                "Field: name. Error: must not be blank. Value: null",
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public void deleteCategory(Long catId) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto) throws ConflictException {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        try {
            CategoryDto categoryDto = modelMapper.toCategoryDto(newCategoryDto);
            categoryDto.setId(catId);
            categoryRepository.updateCategory(catId, categoryDto.getName());
            return categoryRepository.save(categoryDto);
        } catch (ConstraintViolationException e) {
            throw new ConflictException(e.getConstraintName(),
                    "Integrity constraint has been violated.",
                    HttpStatus.CONFLICT
            );
        }
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) throws ConflictException, ValidationException {
        if (newUserRequest.getName() != null) {
            try {
                UserDto userDto = modelMapper.toUserDto(newUserRequest);
                return userRepository.save(userDto);
            } catch (ConstraintViolationException e) {
                throw new ConflictException(e.getConstraintName(),
                        "Integrity constraint has been violated.",
                        HttpStatus.CONFLICT
                );
            }
        } else throw new ValidationException(
                "Field: name. Error: must not be blank. Value: null",
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        userRepository.deleteById(userId);
    }


    @Override
    public List<UserDto> getUsers(List<Long> ids, PageRequest pageRequest) {
        if(ids != null) {
            return userRepository.getUserDtoListByIdIsIn(ids, pageRequest).toList();
        }else {
            return userRepository.findAll(pageRequest).toList();
        }
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) throws ConflictException {
        try {
        //CompilationFullDto compilationFullDtoDto = compilationRepository.save(modelMapper.toCompilationFullDto(newCompilationDto));
        return modelMapper.toCompilationDto(compilationRepository.save(modelMapper.toCompilationFullDto(newCompilationDto)));
        } catch (ConstraintViolationException e) {
            throw new ConflictException(e.getConstraintName(),
                    "Integrity constraint has been violated.",
                    HttpStatus.CONFLICT
            );
        }
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("User with id=" + compId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("User with id=" + compId + " was not found",
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
        CompilationFullDto compilationFullDto = modelMapper.toCompilationFullDto(updateCompilationRequest);
        compilationRepository.updateCompilation(compId, compilationFullDto.getEvents(), updateCompilationRequest.getPinned(), updateCompilationRequest.getTitle());
        return modelMapper.toCompilationDto(compilationRepository.save(modelMapper.toCompilationFullDto(updateCompilationRequest)));
    }

}