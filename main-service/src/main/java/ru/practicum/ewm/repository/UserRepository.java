package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.model.CategoryDto;
import ru.practicum.ewm.model.UserDto;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDto, Long> {

    @Transactional
    @Modifying
    @Query("update CategoryDto " +
            "set name = :name " +
            "where id = :id")
    void updateCategory(@Param("id") Long id, @Param("name") String name);

    @Query(value = "select u.id, u.name from users as u " +
            "where u.id = :id", nativeQuery = true)
    UserShortDto getUserShortDtoById(@Param("id") Long id);

    Page<UserDto> getUserDtoListByIdIsIn(List<Long> ids, Pageable pageable);
}