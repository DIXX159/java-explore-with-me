package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Compilation;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    Compilation findCompilationFullDtoById(Long compId);

    @Query("select c from Compilation as c " +
            "where c.pinned = ?1 " +
            "group by c.id ")
    Page<Compilation> findCompilationFullDtoList(Boolean pinned, Pageable pageable);
}