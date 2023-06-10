package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.model.CompilationFullDto;
import ru.practicum.ewm.model.EventFullEntity;

import java.util.List;

public interface CompilationRepository extends JpaRepository<CompilationFullDto, Long> {

    @Transactional
    @Modifying
    @Query("update CompilationFullDto " +
            "set events = :events, " +
            "pinned = :pinned, " +
            "title = :title " +
            "where id = :id")
    void updateCompilation(@Param("id")Long compId, @Param("events")List<EventFullEntity> events, @Param("pinned")Boolean pinned, @Param("title")String title);

    CompilationFullDto findCompilationFullDtoById(Long compId);

    @Query("select c from CompilationFullDto as c " +
            "where c.pinned = ?1 " +
            "group by c.id ")
    Page<CompilationFullDto> findCompilationFullDtoList(Boolean pinned, Pageable pageable);
}