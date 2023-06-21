package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.CompilationFullDto;

public interface CompilationRepository extends JpaRepository<CompilationFullDto, Long> {

    CompilationFullDto findCompilationFullDtoById(Long compId);

    @Query("select c from CompilationFullDto as c " +
            "where c.pinned = ?1 " +
            "group by c.id ")
    Page<CompilationFullDto> findCompilationFullDtoList(Boolean pinned, Pageable pageable);
}