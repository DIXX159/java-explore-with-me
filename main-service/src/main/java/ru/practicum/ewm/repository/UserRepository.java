package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.model.UserDto;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDto, Long>, QuerydslPredicateExecutor<UserDto> {

    Page<UserDto> getUserDtoListByIdIsIn(List<Long> ids, Pageable pageable);
}