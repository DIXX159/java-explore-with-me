package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.model.CategoryDto;

public interface CategoryRepository extends JpaRepository<CategoryDto, Long> {

    @Transactional
    @Modifying
    @Query("update CategoryDto " +
            "set name = :name " +
            "where id = :id")
    void updateCategory(@Param("id") Long id, @Param("name") String name);

}