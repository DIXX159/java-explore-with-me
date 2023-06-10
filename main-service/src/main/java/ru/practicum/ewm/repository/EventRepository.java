package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.enums.StateAction;
import ru.practicum.ewm.model.EventFullEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventFullEntity, Long> {

    @Query("select e from EventFullEntity as e " +
            "where e.initiator = ?1 " +
            "group by e.id ")
    Page<EventFullEntity> findEventsByUser(Long userId, Pageable pageable);

    Optional<EventFullEntity> findByInitiatorAndId(Long userId, Long eventId);

    EventFullEntity findEventByInitiatorAndId(Long userId, Long eventId);

    EventFullEntity findEventFullEntityById(Long id);

    @Transactional
    @Modifying
    @Query("update EventFullEntity " +
            "set confirmedRequests = :confirmedRequests " +
            "where id = :id")
    void updateRequest(@Param("id") Long id, @Param("confirmedRequests") Long confirmedRequests);
}